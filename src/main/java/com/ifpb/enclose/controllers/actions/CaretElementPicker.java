package com.ifpb.enclose.controllers.actions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class CaretElementPicker extends PsiElementBaseIntentionAction implements IntentionAction {

    @Override
    public @IntentionName @NotNull String getText() {
        return "Show element at caret";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Psi";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return element != null && editor != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        element = element.getParent();
        Messages.showMessageDialog("Icon of " + element.getText(), getText(), element.getIcon(Iconable.ICON_FLAG_VISIBILITY));
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
