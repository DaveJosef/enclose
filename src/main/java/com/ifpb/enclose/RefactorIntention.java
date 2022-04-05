package com.ifpb.enclose;

import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.ifpb.enclose.refactor.CodeChangerImplementation;
import com.ifpb.visitor.MethodCallVisitor;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class RefactorIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    private CodeChangerImplementation codeChanger;

    public RefactorIntention() {
        super();
        this.codeChanger = new CodeChangerImplementation();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        codeChanger.applyChanges();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        codeChanger.setExpression(PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class));
        codeChanger.setProject(project);

        return codeChanger.isAvailable();
    }

    public boolean shouldBeAvailable(Call chosenCall, PsiMethodCallExpression element) {
        if (chosenCall.getCollectionMethod() != null)
            if (chosenCall.getCollectionMethod().getMethodName() != null)
                if (!chosenCall.getCollectionMethod().getMethodName().equals(element.getMethodExpression().getReferenceName())) return false;

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
