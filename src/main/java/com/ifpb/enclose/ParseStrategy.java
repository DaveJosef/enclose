package com.ifpb.enclose;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public interface ParseStrategy {
    public List<PsiMethod> from(Project p, PsiClass classeAlvo);
}
