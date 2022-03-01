package com.ifpb.enclose.view;

import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.intellij.psi.*;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class CallsListTreeModel implements TreeModel {
    private PsiElement rootElement;

    public CallsListTreeModel(PsiElement rootElement) {
        this.rootElement = rootElement;
    }

    @Override
    public Object getRoot() {
        return rootElement;
    }

    @Override
    public Object getChild(Object o, int i) {
        PsiElement psi = (PsiElement) o;
        List<PsiElement> children = getFilteredChildren(psi);
        return children.get(i);
    }

    @Override
    public int getChildCount(Object o) {
        PsiElement psi = (PsiElement) o;
        return getFilteredChildren(psi).size();
    }

    @Override
    public boolean isLeaf(Object o) {
        PsiElement psi = (PsiElement) o;
        return getFilteredChildren(psi).size() == 0;
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) {

    }

    @Override
    public int getIndexOfChild(Object o, Object o1) {
        PsiElement psiParent = (PsiElement) o;
        List<PsiElement> psiChildren = getFilteredChildren(psiParent);
        return psiChildren.indexOf(o1);
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {

    }

    public boolean isValid(PsiElement e) {
        return e != null && (e instanceof PsiDirectory || e instanceof PsiClass || e instanceof PsiMethod || e instanceof PsiFile);
    }

    public List<PsiElement> getFilteredChildren(PsiElement psi) {
        List<PsiElement> filteredChildren = new ArrayList<>();

        if (psi instanceof PsiMethod) {
            VisitorFilter visitor = new VisitorFilter(psi);
            filteredChildren = visitor.getElements();
            return filteredChildren;
        }

        for (PsiElement e = psi.getFirstChild(); e != null; e = e.getNextSibling()) {
            if (isValid(e)) filteredChildren.add(e);
        }
/*
        VisitorFilter elFilter = new VisitorFilter(psi.getFirstChild());
        filteredChildren = elFilter.getElements();
*/
        return filteredChildren;
    }

    private class VisitorFilter extends JavaRecursiveElementVisitor {
        private List<PsiElement> list = new ArrayList<>();

        public VisitorFilter(PsiElement rootElement) {
            rootElement.accept(this);
        }
/*
        @Override
        public void visitMethod(PsiMethod method) {
            list.add(method);
        }

        @Override
        public void visitClass(PsiClass aClass) {
            list.add(aClass);
        }

        @Override
        public void visitJavaFile(PsiJavaFile file) {
            list.add(file);
        }

        @Override
        public void visitDirectory(@NotNull PsiDirectory dir) {
            list.add(dir);
        }
*/
        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            if (expression instanceof PsiReferenceExpression) return;
            Call c = PsiToCallConverter.getCallFrom(expression);
            list.add(expression);
            super.visitMethodCallExpression(expression);
        }

        public List<PsiElement> getElements() {
            return list;
        }

    }
}
