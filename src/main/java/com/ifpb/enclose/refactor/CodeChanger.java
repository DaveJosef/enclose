package com.ifpb.enclose.refactor;

import com.ifpb.enclose.controllers.calls.Call;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public interface CodeChanger {

    void debug();
    void applyChanges();
    boolean isAvailable();
    void setExpression(PsiElement element);
    void setProject(Project project);
    void setChosenCall(Call call);
}
