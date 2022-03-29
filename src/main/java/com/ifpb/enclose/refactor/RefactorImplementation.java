package com.ifpb.enclose.refactor;

import com.ifpb.enclose.controllers.calls.Call;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.ArrayList;
import java.util.List;

public class RefactorImplementation implements Refactor {
    private PsiElement psiExpression;
    private Call chosenCall;
    private Project project;
    private final PsiElementFactory factory;
    private final PsiShortNamesCache cache;

    public RefactorImplementation(PsiElement psiExpression, Call chosenCall, Project project) {
        this.psiExpression = psiExpression;
        this.chosenCall = chosenCall;
        this.project = project;
        this.factory = JavaPsiFacade.getInstance(project).getElementFactory();
        this.cache = PsiShortNamesCache.getInstance(project);
    }

    public void debug() {
        System.out.println("\n(Printing my methods:)\n");

        printPsi(psiExpression);

        PsiElement createdMethod = createMethod();

        printPsi(createdMethod);

        printPsi(createParam(0));

        List<PsiElement> paramList = createParamList();
        printPsiList(paramList);

        List<PsiElement> recursiveParamList = new ArrayList<>();
        printPsiList(createParamList(psiExpression, chosenCall.getTargetMethod().getMethodName(), recursiveParamList, 0));

        List<PsiElement> argsList = createArgsList();
        printPsiList(argsList);

        List<PsiElement> recursiveArgsList = new ArrayList<>();
        printPsiList(createArgsList(psiExpression, chosenCall.getTargetMethod().getMethodName(), recursiveArgsList, 0));

        createdMethod = addParameters(createdMethod, recursiveParamList);

        printPsi(createdMethod);

        PsiElement newExpression = duplicateElement(psiExpression);

        PsiElement newCall = createCall(psiExpression, chosenCall.getTargetMethod().getMethodName());

        printPsi(newCall);

        newCall = replaceArgs(newCall, recursiveParamList, 0);

        printPsi(newCall);

        //newExpression = replaceArgs(newExpression, argsList);

        newExpression = replaceArgs(newExpression, newExpression, chosenCall.getTargetMethod().getMethodName(), recursiveArgsList, 0);

        newExpression = replaceCaller(newExpression, chosenCall.getTargetMethod().getMethodName());

        printPsi(newExpression);

        printPsi(psiExpression);

        createdMethod = createReturn(createdMethod);

        printPsi(createdMethod);

        createdMethod = addReturn(createdMethod, newExpression);

        printPsi(createdMethod);
    }

    public void run() {

    }

    private PsiElement createCall(PsiElement methodExpression, String stopName) {
        String caller = getCaller(methodExpression, stopName).getText();
        String newName = "newMethod";
        PsiElement createdCall = factory.createExpressionFromText(caller+"."+newName+"()", null);

        PsiElement psiList = ((PsiMethodCallExpression) createdCall).getArgumentList();

        List<PsiElement> psiArgs = new ArrayList<>();
        createArgsList(methodExpression, stopName, psiArgs, 0);

        psiArgs.forEach(arg -> {
            psiList.addBefore(arg, psiList.getLastChild());
        });

        return createdCall;
    }

    private PsiElement createReturn(PsiElement method) {
        if (!(method instanceof PsiMethod)) {
            return method;
        }

        PsiCodeBlock psiBlock = ((PsiMethod) method).getBody();
        if (psiBlock == null) {
            return method;
        }

        PsiElement psiReturn = factory.createStatementFromText("return;", null);
        psiBlock.addBefore(psiReturn, psiBlock.getLastChild());

        return method;
    }

    private PsiElement replaceCaller(PsiElement newExpression, String stopName) {

        PsiElement caller = getCaller(newExpression, stopName);
        caller.replace(createThisExpression());

        return newExpression;
    }

    private PsiElement createThisExpression() {
        return factory.createExpressionFromText("this", null);
    }

    private PsiElement getCaller(PsiElement methodExpression, String stopName) {

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return methodExpression;
        }

        PsiElement qualifier = ((PsiMethodCallExpression) methodExpression).getMethodExpression().getQualifierExpression();
        if (qualifier == null) {
            return methodExpression;
        }

        if (!stopName.equals(((PsiMethodCallExpression) methodExpression).getMethodExpression().getText())) {
            return getCaller(qualifier, stopName);
        }

        return methodExpression;
    }

    private List<PsiElement> createParamList(PsiElement methodExpression, String stopName, List<PsiElement> paramList, int s) {

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return paramList;
        }

        PsiElement qualifier = ((PsiMethodCallExpression) methodExpression).getMethodExpression().getQualifierExpression();
        if (qualifier == null) {
            return paramList;
        }

        if (!stopName.equals(((PsiMethodCallExpression) methodExpression).getMethodExpression().getText())) {
            List<PsiElement> thisArgsList = createParamList(methodExpression, s);
            thisArgsList.forEach(e -> {
                paramList.add(e);
            });

            return createParamList(qualifier, stopName, paramList, s+thisArgsList.size());
        }

        return paramList;
    }

    private PsiElement replaceArgs(PsiElement startExpression, PsiElement methodExpression, String stopName, List<PsiElement> paramList, int s) {

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return startExpression;
        }

        PsiElement qualifier = ((PsiMethodCallExpression) methodExpression).getMethodExpression().getQualifierExpression();
        if (qualifier == null) {
            return startExpression;
        }

        if (!stopName.equals(((PsiMethodCallExpression) methodExpression).getMethodExpression().getText())) {
            replaceArgs(methodExpression, paramList, s);
            PsiExpression[] psiList = ((PsiMethodCallExpression) methodExpression).getArgumentList().getExpressions();

            return replaceArgs(startExpression, qualifier, stopName, paramList, s+psiList.length);
        }

        return startExpression;
    }

    private PsiElement replaceArgs(PsiElement newExpression, List<PsiElement> argsList, int s) {
        if (!(newExpression instanceof PsiMethodCallExpression)) {
            return newExpression;
        }

        if (argsList == null) {
            return newExpression;
        }

        PsiExpressionList psiList = ((PsiMethodCallExpression) newExpression).getArgumentList();
        System.out.println(psiList.getExpressionCount());
        int index = s;
        for (int i = 0; i < psiList.getExpressionCount(); i++) {
            psiList.getExpressions()[i].replace(argsList.get(index));
            System.out.println(index);
            index++;
        }

        return newExpression;
    }

    private PsiElement duplicateElement(PsiElement psiExpression) {
        return factory.createExpressionFromText(psiExpression.getText(), null);
    }

    private PsiElement addReturn(PsiElement method, PsiElement psiExpression) {
        if (!(method instanceof PsiMethod)) {
            return method;
        }

        PsiCodeBlock psiBlock = ((PsiMethod) method).getBody();
        if (psiBlock == null) {
            return method;
        }

        if (!(psiBlock.getStatementCount() > 0)) {
            return method;
        }

        PsiStatement psiReturn = psiBlock.getStatements()[psiBlock.getStatementCount() - 1];
        addExpression(psiReturn, psiExpression);

        return method;
    }

    private PsiElement addExpression(PsiStatement psiReturn, PsiElement e) {

        return psiReturn.addBefore(e, psiReturn.getLastChild());
    }

    private PsiElement addParameters(PsiElement method, List<PsiElement> paramList) {
        if (!(method instanceof PsiMethod)) {
            return method;
        }

        PsiParameterList psiList = ((PsiMethod) method).getParameterList();
        if (psiList == null) {
            return method;
        }

        paramList.forEach(e -> {
            addParameter(psiList, e);
        });

        return method;
    }

    private PsiElement addParameter(PsiParameterList psiList, PsiElement e) {

        psiList.addBefore(e, psiList.getLastChild());

        return psiList;
    }

    private List<PsiElement> createArgsList(PsiElement methodExpression, String stopName, List<PsiElement> paramList, int s) {

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return paramList;
        }

        PsiElement qualifier = ((PsiMethodCallExpression) methodExpression).getMethodExpression().getQualifierExpression();
        if (qualifier == null) {
            return paramList;
        }

        if (!stopName.equals(((PsiMethodCallExpression) methodExpression).getMethodExpression().getText())) {
            List<PsiElement> thisArgsList = createArgsList(methodExpression, s);
            thisArgsList.forEach(e -> {
                paramList.add(e);
            });

            return createArgsList(qualifier, stopName, paramList, s+thisArgsList.size());
        }

        return paramList;
    }

    private List<PsiElement> createParamList(PsiElement methodExpression, int s) {
        List<PsiElement> paramList = new ArrayList<>();

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return paramList;
        }

        PsiExpressionList psiList = ((PsiMethodCallExpression) methodExpression).getArgumentList();
        if (psiList == null) {
            return paramList;
        }

        int i = s;
        for (PsiExpression expression :
                psiList.getExpressions()) {
            paramList.add(createArg(i, expression.getType()));
            i++;
        }

        return paramList;
    }

    private List<PsiElement> createArgsList(PsiElement methodExpression, int s) {
        List<PsiElement> paramList = new ArrayList<>();

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return paramList;
        }

        PsiExpressionList psiList = ((PsiMethodCallExpression) methodExpression).getArgumentList();
        if (psiList == null) {
            return paramList;
        }

        int i = s;
        for (PsiExpression expression :
                psiList.getExpressions()) {
            paramList.add(createParam(i));
            i++;
        }

        return paramList;
    }

    private List<PsiElement> createArgsList() {
        return createArgsList(psiExpression, 0);
    }

    private PsiElement createArg(int i, PsiType type) {
        return factory.createParameter("param" + i, type, null);
    }

    private void printPsiList(List<PsiElement> paramList) {
        System.out.println("(Printing the following psi list:)");
        paramList.forEach(e -> {
            printPsi(e);
        });
    }

    private List<PsiElement> createParamList() {
        List<PsiElement> paramList = new ArrayList<>();

        if (!(psiExpression instanceof PsiMethodCallExpression)) {
            return paramList;
        }

        PsiExpressionList psiList = ((PsiMethodCallExpression) psiExpression).getArgumentList();
        if (psiList == null) {
            return paramList;
        }

        int i = 0;
        for (PsiExpression expression :
                psiList.getExpressions()) {
            paramList.add(createArg(i, expression.getType()));
            i++;
        }

        return paramList;
    }

    private PsiElement createParam(int i) {
        return factory.createExpressionFromText("param" + i, null);
    }

    private void printPsi(PsiElement e) {
        if (e == null) {
            return;
        }

        System.out.println(e.getText() + "\n");
    }

    private PsiElement createMethod() {
        if (!(psiExpression instanceof PsiMethodCallExpression)) {
            return null;
        }

        PsiType psiType = ((PsiMethodCallExpression) psiExpression).getType();

        return factory.createMethod("newMethod", psiType, null);
    }
}
