package com.ifpb.view;

import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
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

    private class ElementVisitor extends PsiElementVisitor {

        public ElementVisitor() {
        }

        public void visitDirectory(@NotNull PsiDirectory dir) {
            setIcon(dir.getIcon(Iconable.ICON_FLAG_READ_STATUS));
            setText(dir.getName());
        }

        @Override
        public void visitFile(@NotNull PsiFile file) {
            setIcon(file.getIcon(Iconable.ICON_FLAG_READ_STATUS));
            setText(file.getName());
        }
    }
}
