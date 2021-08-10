package com.ifpb.enclose;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.lang.LanguageRefactoringSupport;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class RefactorIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

        PsiMethodCallExpression parent = (PsiMethodCallExpression) element.getParent().getParent();
        Messages.showMessageDialog(project, parent.getText(), "Selected PsiElement:", Messages.getInformationIcon());
        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);

        PsiReferenceExpression expr = (PsiReferenceExpression) factory.createExpressionFromText("a.yourNewMethod", null);

        element.getParent().replace(expr);
        Messages.showMessageDialog(project, element.getParent().getText(), "Selected PsiElement:", Messages.getInformationIcon());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element == null) {
            return false;
        }
        if ((element.getParent() instanceof PsiReferenceExpression) && (element.getParent().getFirstChild() instanceof PsiMethodCallExpression)) {
            return true;
        }

        return false;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "RefactorIntention";
    }

    @NotNull
    public String getText() {
        return "Refactor violations of Law Of Demeter";
    }
}
