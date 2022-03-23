package com.ifpb.enclose.controllers.actions;

import com.ifpb.enclose.PsiMethodCallExpressionUtils;
import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.ifpb.enclose.view.CallsListPanel;
import com.ifpb.visitor.MethodCallVisitor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteActionAware;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class RefactorCallAction extends AnAction {
    private final Project project;
    private PsiElement element;
    private CallsListPanel myListerPanel;
    int refactorCount = 0;
    private CallList calllist = new CallList();

    public RefactorCallAction(String actionName, String toolTip, Icon icon, Project project, PsiElement element, CallsListPanel myListerPanel) {
        super(actionName, toolTip, icon);
        this.project = project;
        this.element = element;
        this.myListerPanel = myListerPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Call chosenCall = chooseCall(element);
        System.out.println("Chosen Call: " + chosenCall);
        System.out.println("Element: " + element);
        System.out.println("Parent call expression: " + PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class));

        //if (!(chosenCall == null))
        ApplicationManager.getApplication().invokeLater( () -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                refactor(project, chosenCall);
                myListerPanel.applyBreakerFilter();
            });
        });


        System.out.println(this);
    }

    private Call chooseCall(PsiElement element) {

        if (element == null) {
            return null;
        }

        if (element instanceof PsiMethodCallExpression) {
            return PsiToCallConverter.getCallFrom((PsiMethodCallExpression) element);
        }

        PsiMethodCallExpression chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (chamada == null) return null;
/*
        // --- COM VISITOR
        MethodCallVisitor visitor = new MethodCallVisitor();
        Path path = Paths.get(project.getBasePath());
        VirtualFile pastaDoProjeto = VfsUtil.findFile(path, true);
        PsiDirectory dir = PsiManager.getInstance(project).findDirectory(pastaDoProjeto);
        if (dir != null) dir.accept(visitor);
        this.calllist = new CallList(visitor.getVisitResult());

        if (!this.calllist.contains(PsiToCallConverter.getCallFrom(chamada))) {
            return null;
        }
*/
        //if (!PsiToCallConverter.getCallFrom(chamada).isComplete()) return false;

        return PsiToCallConverter.getCallFrom(chamada);
    }

    void refactor(Project project, Call chosenCall) {

        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);
        final PsiMethodCallExpressionUtils util = new PsiMethodCallExpressionUtils();
        final PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);

        String[] classeA = chosenCall.getClientClass().split("[.]");
        PsiClass classeCliente = cache.getClassesByName(classeA[classeA.length - 1], GlobalSearchScope.allScope(project))[0];

        PsiMethodCallExpression chamada;
        if (!(element instanceof PsiMethodCallExpression)) {
            chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        } else {
            chamada = (PsiMethodCallExpression) element;
        }

        /* reduzir chamada até .add() */
        while (!(chamada.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression) && !chamada.getMethodExpression().getReferenceName().equals(chosenCall.getCollectionMethod().getMethodName())) {
            chamada = (PsiMethodCallExpression) chamada.getMethodExpression().getQualifierExpression();
        }

        /* Coletar as PsiParameterLists */
        List<PsiExpressionList> listOfArguments = new ArrayList<>();
        PsiMethodCallExpression p = chamada;
        listOfArguments.add(p.getArgumentList());
        if (!(p.getMethodExpression().getQualifierExpression() instanceof PsiMethodCallExpression)) return;
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

        /* Print methods in class
        PsiMethod[] dgbMethods = classeAlvo.getMethods();
        System.out.println("Without Visitor");
        for (int k = 0; i < dgbMethods.length; i++) {
            System.out.println(dgbMethods[k]);
        }
        System.out.println("\n");*/

        /* Print methods in class while using Visitor
        System.out.println("With Visitor");
        classeAlvo.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                System.out.println(method);
            }
        });
        System.out.println("\n");*/

        /* Print methods in class while using MethodVisitor
        System.out.println("With MethodVisitor");
        MethodVisitor visitor = new MethodVisitor();
        classeAlvo.accept(visitor);
        visitor.debug();
        System.out.println("\n");*/

        /* Print visitor visits
        System.out.print("With the PrintMethodVisitor");
        PrintMethodVisitor visitorString = new PrintMethodVisitor();
        classeAlvo.accept(visitorString);
        System.out.println(visitorString.visitToString());
        System.out.println("\n");*/

        /* Print methods in each file of open project
        System.out.println("With the SmartParseMethod");
        MethodVisitor parserVisitor = new MethodVisitor();
        List<PsiMethod> methodParser = new SmartParseMethod().visitor(parserVisitor).from(project, classeAlvo);
        parserVisitor.debug();
        System.out.println("\n");*/

        Messages.showMessageDialog("Novo metodo criado em A.java!", "Método criado", classeAlvo.getIcon(Iconable.ICON_FLAG_VISIBILITY));
    }

    public void setRefactoringElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return super.toString() +
                " project: " + project +
                " element: " + element + " ";
    }

}
