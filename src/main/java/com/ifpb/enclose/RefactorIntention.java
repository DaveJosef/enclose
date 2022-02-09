package com.ifpb.enclose;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RefactorIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    private CallList calllist = new CallList();
    private Call chosenCall = new Call();
    private int refactorCount = 0;
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);
        final PsiMethodCallExpressionUtils util = new PsiMethodCallExpressionUtils();
        final PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);

        PsiClass classeCliente = cache.getClassesByName(chosenCall.clientClass(), GlobalSearchScope.allScope(project))[0];
        PsiMethodCallExpression chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);

        /* reduzir chamada até .add() */
        while (!(chamada.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression) && !chamada.getMethodExpression().getReferenceName().equals(chosenCall.collectionMethod())) {
            chamada = (PsiMethodCallExpression) chamada.getMethodExpression().getQualifierExpression();
        }

        /* Coletar as PsiParameterLists */
        List<PsiExpressionList> listOfArguments = new ArrayList<>();
        PsiMethodCallExpression p = chamada;
            listOfArguments.add(p.getArgumentList());
            p = (PsiMethodCallExpression) p.getMethodExpression().getQualifierExpression();
        listOfArguments.add(p.getArgumentList());

        /* Criar metodo estatico em A.java */
        PsiType tipoDeRetorno = chamada.getType();
        PsiMethod newMethod = factory.createMethod("newMethod"+this.refactorCount, tipoDeRetorno);
        PsiMethodCallExpression newChamada = (PsiMethodCallExpression) factory.createExpressionFromText(chamada.getText(), null);
        PsiStatement newReturnStatement = factory.createStatementFromText("return;", null);

        /* lista de novos parâmetros */
        List<PsiParameter> listOfParameters = new ArrayList<>();
        int i = 0;
        for (PsiExpressionList PsiExpressionListN : listOfArguments) {
            for (PsiExpression expr : PsiExpressionListN.getExpressions()) {
                PsiType newTipo = expr.getType();
                i++;
                String newParameterName = "newParameter" + i;
                PsiParameter newParameter = factory.createParameter(newParameterName, newTipo);
                listOfParameters.add(newParameter);
            }
        }

        listOfParameters.forEach(newParameter -> {
            newMethod.getParameterList().addBefore(newParameter, newMethod.getParameterList().getLastChild());
        });

        /* Alterando newChamada */
        int j=0;
        PsiMethodCallExpression q = newChamada;
            for (PsiExpression expr : q.getArgumentList().getExpressions()) {
                if (expr != null) {
                    PsiExpression newReferenceExpression = factory.createExpressionFromText(listOfParameters.get(j).getName(), null);
                    expr.replace(newReferenceExpression);
                    j++;
                }
            }
            q = (PsiMethodCallExpression) q.getMethodExpression().getQualifierExpression();
        for (PsiExpression expr : q.getArgumentList().getExpressions()) {
            if (expr != null) {
                PsiExpression newReferenceExpression = factory.createExpressionFromText(listOfParameters.get(j).getName(), null);
                expr.replace(newReferenceExpression);
                j++;
            }
        }
        PsiThisExpression newThisElement = (PsiThisExpression) factory.createExpressionFromText("this", null);
        q.getMethodExpression().getQualifierExpression().replace(newThisElement);

        newReturnStatement.addBefore(newChamada, newReturnStatement.getLastChild());
        newMethod.getBody().addBefore(newReturnStatement, newMethod.getBody().getLastChild());

        PsiMethodCallExpression r = (PsiMethodCallExpression) chamada.getMethodExpression().getQualifierExpression();
        PsiClass classeAlvo = cache.getClassesByName(r.getMethodExpression().getQualifierExpression().getType().getPresentableText(false), GlobalSearchScope.allScope(project))[0];
        classeAlvo.addBefore(newMethod, classeAlvo.getLastChild());

        PsiMethodCallExpression newChamadaEmC = (PsiMethodCallExpression) factory.createExpressionFromText(r.getMethodExpression().getQualifierExpression().getText()+"."+newMethod.getName()+"()", null);
        for (PsiExpressionList PsiExpressionListN : listOfArguments) {
            for (PsiExpression expr : PsiExpressionListN.getExpressions()) {
                newChamadaEmC.getArgumentList().addBefore(expr, newChamadaEmC.getArgumentList().getLastChild());
            }
        }
        chamada.replace(newChamadaEmC);

        this.refactorCount++;

        /* Print methods in class */
        PsiMethod[] dgbMethods = classeAlvo.getMethods();
        System.out.println("Without Visitor");
        for (int k = 0; i < dgbMethods.length; i++) {
            System.out.println(dgbMethods[k]);
        }
        System.out.println("\n");

        /* Print methods in class while using Visitor */
        System.out.println("With Visitor");
        classeAlvo.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                System.out.println(method);
            }
        });
        System.out.println("\n");

        /* Print methods in class while using MethodVisitor */
        System.out.println("With MethodVisitor");
        MethodVisitor visitor = new MethodVisitor();
        classeAlvo.accept(visitor);
        visitor.debug();
        System.out.println("\n");

        /* Print visitor visits */
        System.out.print("With the PrintMethodVisitor");
        PrintMethodVisitor visitorString = new PrintMethodVisitor();
        classeAlvo.accept(visitorString);
        System.out.println(visitorString.visitToString());
        System.out.println("\n");

        /* Print methods in each file of open project */
        System.out.println("With the SmartParseMethod");
        MethodVisitor parserVisitor = new MethodVisitor();
        List<PsiMethod> methodParser = new SmartParseMethod().visitor(parserVisitor).from(project, classeAlvo);
        parserVisitor.debug();
        System.out.println("\n");
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element == null) {
            return false;
        }
        PsiMethodCallExpression chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (chamada == null || containingMethod == null) {
            return false;
        }

        /* Escolher uma call */
        for (Call c : calllist.calls()) {
            if (chamada.getText().contains("." + c.collectionMethod() + "(")) {
                this.chosenCall = c;
            }
        }

        /* reduzir chamada até .add() */
        while (!(chamada.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression) && !chamada.getMethodExpression().getReferenceName().equals(chosenCall.collectionMethod())) {
            chamada = (PsiMethodCallExpression) chamada.getMethodExpression().getQualifierExpression();
        }
        if (!(chamada.getMethodExpression().getQualifierExpression() instanceof PsiMethodCallExpression)) {
            return false;
        }
        if (chamada.getType() == null) {
            return false;
        }
        if (!element.getText().equals(chosenCall.collectionMethod())) {
            return false;
        }

        return true;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "RefactorIntention";
    }

    @NotNull
    public String getText() {
        return "Refactor violations of Law Of Demeter";
    }

    public boolean startInWriteAction() {
        return true;
    }
}
