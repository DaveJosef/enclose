package com.ifpb.enclose.view;

import com.intellij.openapi.util.Iconable;
import com.intellij.psi.*;
import icons.MyPluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CallsListTreeCellRenderer extends DefaultTreeCellRenderer {
    private final ElementVisitor visitor = new ElementVisitor();

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setIcon(((PsiElement) value).getIcon(Iconable.ICON_FLAG_READ_STATUS));
        ((PsiElement) value).accept(visitor);
        return this;
    }

    private class ElementVisitor extends JavaRecursiveElementVisitor {

        public ElementVisitor() {
        }

        @Override
        public void visitDirectory(@NotNull PsiDirectory dir) {
            setIcon(dir.getIcon(Iconable.ICON_FLAG_VISIBILITY));
            setText(dir.getName());
        }

        @Override
        public void visitFile(@NotNull PsiFile file) {
            setIcon(file.getIcon(Iconable.ICON_FLAG_VISIBILITY));
            setText(file.getName());
        }

        @Override
        public void visitMethod(PsiMethod method) {
            setIcon(method.getIcon(Iconable.ICON_FLAG_VISIBILITY));
            setText(method.getName());
        }

        @Override
        public void visitClass(PsiClass aClass) {
            setIcon(aClass.getIcon(Iconable.ICON_FLAG_VISIBILITY));
            setText(aClass.getName());
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            setIcon(MyPluginIcons.ListCallsAction);
            setText(expression.getText());
        }
    }
}
