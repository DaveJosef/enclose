package com.ifpb.tests;

import com.ifpb.calls.Call;
import com.ifpb.calls.CallList;
import com.ifpb.enclose.RefactorIntention;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NotNull;

public class PluginPsiTests extends AnAction {
    String m = "";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        this.m += "\n" + shouldBeAvailable(e.getProject());

        Messages.showInfoMessage(m, "TestsResult:");
    }

    public String shouldBeAvailable(Project project) {
        Call c = new Call();
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiMethodCallExpression chamada = (PsiMethodCallExpression) factory.createExpressionFromText("a.getElements(this).add(new A())", null);
        PsiMethodCallExpression element = (PsiMethodCallExpression) factory.createExpressionFromText("add(new A())", null);

        String testResult = new StringBuilder("call: ").append(c).append("\n")
                .append("chamada: ").append(chamada).append("\n")
                .append("tipo da chamada: ").append(chamada.getType() + "").append("\n")
                .append("element: ").append(element).append("\n").toString();

        return testResult + new RefactorIntention().shouldBeAvailable(c, element) + "";
    }
}
