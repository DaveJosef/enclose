package com.ifpb.enclose;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodVisitor extends JavaRecursiveElementVisitor {

    private List<PsiMethod> mds;

    public MethodVisitor() {
        this(new ArrayList<>());
    }

    public MethodVisitor(List<PsiMethod> mds) {
        this.mds = mds;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        mds.add(method);
        System.out.println("Adding " + method.toString() + "...");
        super.visitMethod(method);
    }

    private List<PsiMethod> getMDS() {
        return Collections.unmodifiableList(mds);
    }

    private String getMethod(int count, String methodName) {
        if (count < 1) return null;

        if (!mds.get(count - 1).getName().contains(methodName)) return null;

        return mds.get(count - 1).getName();
    }

    public void debug() {
        System.out.println(getMDS());
        //System.out.println(getMethod(1, "getElements"));
    }
}
