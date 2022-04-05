package com.ifpb.enclose.controllers.actions;

import com.ifpb.enclose.PsiMethodCallExpressionUtils;
import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.ifpb.enclose.refactor.CodeChangerImplementation;
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
    private CodeChangerImplementation codeChanger;

    public RefactorCallAction(String actionName, String toolTip, Icon icon, Project project, PsiElement element, CallsListPanel myListerPanel) {
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

    private void refactor() {
        codeChanger.setExpression(element);
        codeChanger.setProject(project);

        if (!codeChanger.isAvailable()) {
            return;
        }

        ApplicationManager.getApplication().invokeLater( () -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                codeChanger.applyChanges();
                myListerPanel.applyBreakerFilter();
            });
        });

        System.out.println(this);
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
