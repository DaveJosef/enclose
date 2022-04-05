package com.ifpb.enclose.controllers.actions;

import com.ifpb.enclose.PsiMethodCallExpressionUtils;
import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.ifpb.enclose.refactor.CodeChangerImplementation;
import com.ifpb.enclose.view.CallsListPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RefactorAllCallsAction extends AnAction {
    private final Project project;
    private PsiElement element;
    private CallsListPanel myListerPanel;
    private CodeChangerImplementation codeChanger;
    private int refactorCount = 0;

    public RefactorAllCallsAction(String actionName, String toolTip, Icon icon, Project project, PsiElement element, CallsListPanel myListerPanel) {
        super(actionName, toolTip, icon);
        this.project = project;
        this.element = element;
        this.myListerPanel = myListerPanel;
        this.codeChanger = new CodeChangerImplementation();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        refactor();
    }

    void refactor() {
        this.refactorCount = 0;
        List<PsiElement> elements;

        System.out.println(myListerPanel.getCallNodes());
        elements = myListerPanel.getCallNodes();

        codeChanger.setProject(project);

        ApplicationManager.getApplication().invokeLater( () -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                for (PsiElement element :
                        elements) {
                    this.element = element;

                    codeChanger.setExpression(element);

                    if (!codeChanger.isAvailable()) {
                        return;
                    }

                    codeChanger.applyChanges();

                    countRefactor();
                }

                myListerPanel.applyBreakerFilter();
            });
        });

        System.out.println(this);
        System.out.println(refactorCount + " mudanças realizadas!");
    }

    private void countRefactor() {
        this.refactorCount++;
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
