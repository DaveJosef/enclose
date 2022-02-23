package com.ifpb.visitor;

import com.ifpb.calls.Call;
import com.ifpb.calls.PsiToCallConverter;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.ArrayList;
import java.util.List;

public class MethodCallVisitor extends JavaRecursiveElementVisitor {
    List<Call> calls = new ArrayList<>();

    @Override
    public void visitMethodCallExpression(PsiMethodCallExpression expression) {
        super.visitMethodCallExpression(expression);

        Call currentCall = PsiToCallConverter.getCallFrom(expression);
        this.calls.add(currentCall);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        calls.forEach(element -> {
            string.append(element.toString()).append("\n");
        });

        return string.toString();
    }

    public List<Call> getVisitResult() {
        return this.calls;
    }
}
