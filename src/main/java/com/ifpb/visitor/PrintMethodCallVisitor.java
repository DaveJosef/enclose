package com.ifpb.visitor;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;

public class PrintMethodCallVisitor extends JavaRecursiveElementVisitor {
    private String currentVisit;
    private String lastVisit = "";
    private StringBuilder builder = new StringBuilder();

    public PrintMethodCallVisitor() {
        this(new StringBuilder());
    }

    public PrintMethodCallVisitor(StringBuilder builder) {
        this.builder = builder;
    }

    public String visitToString() {
        return this.builder.toString();
    }

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        this.currentVisit = expression.getText();
        //if (!this.lastVisit.contains(this.currentVisit))
            builder.append(expression.getTextOffset()).append(" ").append(expression.getText()).append("\n");

        this.lastVisit = this.currentVisit;
        super.visitMethodCallExpression(expression);
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
