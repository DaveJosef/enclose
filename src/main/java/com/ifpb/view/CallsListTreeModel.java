package com.ifpb.view;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiEditorUtil;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class CallsListTreeModel implements TreeModel {
    private PsiElement rootElement;

    @Override
    public Object getRoot() {
        return rootElement;
    }

    @Override
    public Object getChild(Object o, int i) {
        return null;
    }

    @Override
    public int getChildCount(Object o) {
        return 0;
    }

    @Override
    public boolean isLeaf(Object o) {
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) {

    }

    @Override
    public int getIndexOfChild(Object o, Object o1) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {

    }

    public boolean isValid(PsiElement psiElement) {
        return !(psiElement instanceof PsiWhiteSpace);
    }

    public List<PsiElement> getFilteredChildren(PsiElement psi) {
        final List<PsiElement> filteredChildren = new ArrayList<>();

        for (PsiElement e = psi.getFirstChild(); e != null; e = e.getNextSibling()) {
            if (isValid(e)) filteredChildren.add(e);
        }

        return filteredChildren;
    }
}
