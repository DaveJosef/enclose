package com.ifpb.visitor;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;

public class PrintMethodVisitor extends JavaRecursiveElementVisitor {
    private StringBuilder builder = new StringBuilder();

    public String visitToString() {
        return this.builder.toString();
    }

    @Override
    public void visitMethod(PsiMethod method) {
        /*
        System.out.print(" and Reflection\n");
        class Local {
        };
        builder.append(Local.class.getEnclosingMethod().getName()).append("/").append(Local.class.getEnclosingMethod().getParameters()[0].getType().getName()).append("\n");
        */
        System.out.print(" without Reflection\n");
        builder.append("\n").append(method.getText()).append("\n");
        super.visitMethod(method);
    }
}
