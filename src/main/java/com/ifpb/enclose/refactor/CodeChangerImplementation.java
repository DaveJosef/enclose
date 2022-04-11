package com.ifpb.enclose.refactor;

import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.ifpb.visitor.MethodCallVisitor;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateMethodFromMethodReferenceFix;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateMethodFromUsageFix;
import com.intellij.lang.LanguageRefactoringSupport;
import com.intellij.lang.jvm.actions.CreateMethodActionGroup;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringSettings;
import com.intellij.refactoring.actions.RefactoringActionContextUtil;
import com.intellij.refactoring.util.RefactoringMessageDialog;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CodeChangerImplementation implements CodeChanger {
    private PsiElement psiExpression;
    private Call chosenCall;
    private Project project;
    private PsiElementFactory factory;
    private PsiShortNamesCache cache;
    private int changesCount = 0;

    public CodeChangerImplementation() {
    }

    public CodeChangerImplementation(PsiElement psiExpression, Call chosenCall, Project project) {
        this.psiExpression = psiExpression;
        this.chosenCall = chosenCall;
        this.project = project;
        this.factory = JavaPsiFacade.getInstance(project).getElementFactory();
        this.cache = PsiShortNamesCache.getInstance(project);
    }

    @Override
    public void debug() {
        System.out.println("\n(Printing my methods:)\n");

        printPsi(psiExpression); // getA().getElements(this).set(0, new A())

        PsiElement createdMethod = createMethod(false);

        printPsi(createdMethod); // public com.ifpb.A newMethod() {}

        printPsi(createParam(0)); // param0

        List<PsiElement> paramList = createParamList();
        printPsiList(paramList);
//
//        (Printing the following psi list:)
//        int param0
//
//        com.ifpb.A param1

        List<PsiElement> recursiveParamList = new ArrayList<>();
        printPsiList(createParamList(psiExpression, chosenCall.getTargetMethod().getMethodName(), recursiveParamList, 0));
//
//        (Printing the following psi list:)
//        int param0
//
//        com.ifpb.A param1
//
//        com.ifpb.C param2

        List<PsiElement> argsList = createArgsList();
        printPsiList(argsList);
//
//        (Printing the following psi list:)
//        param0
//
//                param1

        List<PsiElement> recursiveArgsList = new ArrayList<>();
        printPsiList(createArgsList(psiExpression, chosenCall.getTargetMethod().getMethodName(), recursiveArgsList, 0));
//
//        (Printing the following psi list:)
//        param0
//
//                param1
//
//        param2

        createdMethod = addParameters(createdMethod, recursiveParamList);

        printPsi(createdMethod); // public com.ifpb.A newMethod(int param0, com.ifpb.A param1, com.ifpb.C param2) {}

        PsiElement newExpression = duplicateElement(psiExpression);

        printPsi(newExpression); // this.getElements(param2).set(param0, param1)

        PsiElement newCall = createCall(psiExpression, chosenCall.getTargetMethod().getMethodName());

        printPsi(newCall); // getA().newMethod(param0, param1, param2)

        List<PsiElement> oldArgs = new ArrayList<>();
        printPsiList(collectArgs(oldArgs));
//
//        (Printing the following psi list:)
//        0
//
//                new A()
//
//        this

        newCall = replaceArgs(newCall, oldArgs, 0);

        printPsi(newCall); // getA().newMethod(0, new A(), this)

        //newExpression = replaceArgs(newExpression, argsList);

        newExpression = replaceArgs(newExpression, newExpression, chosenCall.getTargetMethod().getMethodName(), recursiveArgsList, 0);

        newExpression = replaceCaller(newExpression, chosenCall.getTargetMethod().getMethodName(), false);

        printPsi(newExpression); // this.getElements(param2).set(param0, param1)

        printPsi(psiExpression); // getA().getElements(this).set(0, new A())

        createdMethod = createReturn(createdMethod);

        printPsi(createdMethod); // public com.ifpb.A newMethod(int param0, com.ifpb.A param1, com.ifpb.C param2) {return;}

        createdMethod = addReturn(createdMethod, newExpression);

        printPsi(createdMethod); // public com.ifpb.A newMethod(int param0, com.ifpb.A param1, com.ifpb.C param2) {return this.getElements(param2).set(param0, param1);}
    }

    @Override
    public void applyChanges() {
        PsiClass targetClass = searchClass(getSimplifiedClass(chosenCall.getTargetClass()));

        if (targetClass == null) {
            return;
        }

        boolean isMethodStatic = false;
        if (!chosenCall.getTargetClass().contains(".")) {
            isMethodStatic = true;
        }
//
//        PsiElement createdMethod = createMethod(isMethodStatic);
//        printPsi(createdMethod); // public com.ifpb.A newMethod() {}

        List<PsiElement> recursiveParamList = new ArrayList<>();
        printPsiList(createParamList(psiExpression, chosenCall.getTargetMethod().getMethodName(), recursiveParamList, 0));
//
//        (Printing the following psi list:)
//        int param0
//
//        com.ifpb.A param1
//
//        com.ifpb.C param2
//
//        createdMethod = addParameters(createdMethod, recursiveParamList);
//        printPsi(createdMethod); // public com.ifpb.A newMethod(int param0, com.ifpb.A param1, com.ifpb.C param2) {}
//
//        createdMethod = createStatement(createdMethod);
//        printPsi(createdMethod); // public com.ifpb.A newMethod(int param0, com.ifpb.A param1, com.ifpb.C param2) {return;}

        PsiElement newExpression = duplicateElement(psiExpression);
        printPsi(newExpression); // this.getElements(param2).set(param0, param1)

        List<PsiElement> recursiveArgsList = new ArrayList<>();
        printPsiList(createArgsList(psiExpression, chosenCall.getTargetMethod().getMethodName(), recursiveArgsList, 0));
//
//        (Printing the following psi list:)
//        param0
//
//                param1
//
//        param2

        newExpression = replaceArgs(newExpression, newExpression, chosenCall.getTargetMethod().getMethodName(), recursiveArgsList, 0);

        newExpression = replaceCaller(newExpression, chosenCall.getTargetMethod().getMethodName(), isMethodStatic);
        printPsi(newExpression); // this.getElements(param2).set(param0, param1)
//
//        createdMethod = addReturn(createdMethod, newExpression);
//        printPsi(createdMethod); // public com.ifpb.A newMethod(int param0, com.ifpb.A param1, com.ifpb.C param2) {return this.getElements(param2).set(param0, param1);}
//
//        targetClass.addBefore(createdMethod, targetClass.getLastChild());

        // client class transformation

        PsiElement newCall = createCall(psiExpression, chosenCall.getTargetMethod().getMethodName());
        printPsi(newCall); // getA().newMethod(param0, param1, param2)

        List<PsiElement> oldArgs = new ArrayList<>();
        printPsiList(collectArgs(oldArgs));
//
//        (Printing the following psi list:)
//        0
//
//                new A()
//
//        this

        newCall = replaceArgs(newCall, oldArgs, 0);
        printPsi(newCall); // getA().newMethod(0, new A(), this)

        psiExpression.replace(newCall);

        // create method in target class with gui
        PsiClass clientClass = searchClass(getSimplifiedClass(chosenCall.getClientClass()));

        //CreateMethodFromUsageFix.createMethod(targetClass, clientClass, null, "newMethod");

        // update changes counter
        changesCount++;
    }

    private PsiElement createStatement(PsiElement method) {
        if (!(method instanceof PsiMethod)) {
            return method;
        }

        if (!(((PsiMethod) method).getReturnType().getCanonicalText() == "void")) {

            createReturn(method);

            return method;
        }

        PsiCodeBlock psiBlock = ((PsiMethod) method).getBody();
        if (psiBlock == null) {
            return method;
        }

        PsiElement psiReturn = factory.createStatementFromText(";", null);
        psiBlock.addBefore(psiReturn, psiBlock.getLastChild());

        return method;
    }

    private String getSimplifiedClass(String classInCall) {
        String[] namePieces = classInCall.split("[<]");
        String className = namePieces[0];
        namePieces = className.split("[.]");
        return namePieces[namePieces.length - 1];
    }

    @Override
    public boolean isAvailable() {

        if (psiExpression == null) {
            return false;
        }

        chooseCall();

        if (chosenCall == null) {
            return false;
        }

        if (chosenCall.getTargetClass() == null) {
            return false;
        }

        if (project == null) {
            return false;
        }


        return true;
    }

    @Override
    public void setExpression(PsiElement element) {
        this.psiExpression = element;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
        this.factory = JavaPsiFacade.getInstance(project).getElementFactory();
        this.cache = PsiShortNamesCache.getInstance(project);
    }

    @Override
    public void setChosenCall(Call call) {
        this.chosenCall = call;
        System.out.println("Chosen Call: " + chosenCall);
    }

    private void chooseCall() {
        setChosenCall(PsiToCallConverter.getCallFrom((PsiMethodCallExpression) psiExpression));
    }

    private PsiClass searchClass(String name) {

        PsiClass[] classes = cache.getClassesByName(name, GlobalSearchScope.allScope(project));
        if (classes.length < 1) {
            return null;
        }

        return classes[0];
    }

    private List<PsiElement> collectArgs(List<PsiElement> argsList) {

        return collectArgs(psiExpression, chosenCall.getTargetMethod().getMethodName(), argsList, 0);
    }

    private List<PsiElement> collectArgs(PsiElement methodExpression, String stopName, List<PsiElement> argsList, int s) {

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return argsList;
        }

        PsiElement qualifier = ((PsiMethodCallExpression) methodExpression).getMethodExpression().getQualifierExpression();
        if (qualifier == null) {
            return argsList;
        }

        if (!stopName.equals(((PsiMethodCallExpression) methodExpression).getMethodExpression().getText())) {
            List<PsiElement> thisArgsList = collectArgs(methodExpression, s);
            thisArgsList.forEach(e -> {
                argsList.add(e);
            });

            return collectArgs(qualifier, stopName, argsList, s+thisArgsList.size());
        }

        return argsList;
    }

    private List<PsiElement> collectArgs(PsiElement methodExpression, int s) {
        List<PsiElement> argsList = new ArrayList<>();

        if (!(methodExpression instanceof PsiMethodCallExpression)) {
            return argsList;
        }

        PsiExpressionList psiList = ((PsiMethodCallExpression) methodExpression).getArgumentList();
        if (psiList == null) {
            return argsList;
        }

        int i = s;
        for (PsiExpression expression :
                psiList.getExpressions()) {
            argsList.add(expression);
            i++;
        }

        return argsList;
    }

    private PsiElement createCall(PsiElement methodExpression, String stopName) {
        String caller = getCaller(methodExpression, stopName).getText();
        String newName = "newMethod" + changesCount;
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

    private PsiElement replaceCaller(PsiElement newExpression, String stopName, boolean isMethodStatic) {

        PsiElement caller = getCaller(newExpression, stopName);

        if (!isMethodStatic) {
            caller.replace(createThisExpression());

            return newExpression;
        }

        caller.delete();

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

        if (!stopName.equals(((PsiMethodCallExpression) methodExpression).getMethodExpression().getReferenceName())) {
            System.out.println(((PsiMethodCallExpression) methodExpression).getMethodExpression().getReferenceName());
            return getCaller(qualifier, stopName);
        }

        return qualifier;
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

    private PsiElement createMethod(boolean isMethodStatic) {
        if (!(psiExpression instanceof PsiMethodCallExpression)) {
            return null;
        }

        PsiType psiType = ((PsiMethodCallExpression) psiExpression).getType();

        PsiMethod psiMethod = factory.createMethod("newMethod" + changesCount, psiType, null);

        if (!isMethodStatic) {
            return psiMethod;
        }

        PsiModifierList psiModifiers = psiMethod.getModifierList();
        psiModifiers.addBefore(factory.createKeyword("static"), psiModifiers.getLastChild());

        return psiMethod;
    }
}
