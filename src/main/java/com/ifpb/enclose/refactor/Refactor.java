package com.ifpb.enclose.refactor;

import com.ifpb.enclose.controllers.calls.Call;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public interface Refactor {
    PsiElement psiExpression = null;
    Call chosenCall = null;
    Project project = null;
    PsiElementFactory factory = null;

    void debug();

    void run();
}
