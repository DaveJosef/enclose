package com.ifpb.processor;

import com.ifpb.visitor.PrintMethodCallVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.util.Processor;

public class JavaClassesProcessor implements Processor<PsiClass> {
    PrintMethodCallVisitor printVisitor = new PrintMethodCallVisitor();

    @Override
    public boolean process(PsiClass psiClass) {
        System.out.println("Processing: " + psiClass.getQualifiedName());
        psiClass.accept(this.printVisitor);
        return true;
    }

    public String processToString() {
        return this.printVisitor.visitToString();
    }
}
