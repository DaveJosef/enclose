package com.ifpb.enclose;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.ArrayList;
import java.util.List;

public class SmartParseMethod implements ParseMethod {
    private List<PsiMethod> mds = new ArrayList<>();
    private MethodVisitor visitor;

    public SmartParseMethod() {
        this.visitor = new MethodVisitor(mds);
    }

    public SmartParseMethod(MethodVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public List<PsiMethod> from(Project p, PsiClass classeAlvo) {
        PsiDirectory dir = classeAlvo.getContainingFile().getContainingDirectory();
        PsiFile[] files = dir.getFiles();
        List<PsiFile> filesList = List.of(files);
        filesList.forEach(file -> {
            file.accept(visitor);
        });
        return mds;
    }

    @Override
    public ParseMethod visitor(MethodVisitor visitor) {
        this.visitor = visitor;
        return this;
    }
}
