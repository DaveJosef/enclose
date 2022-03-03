package com.ifpb.enclose.view;

import com.ifpb.enclose.controllers.calls.Call;
import com.ifpb.enclose.controllers.calls.PsiToCallConverter;
import com.ifpb.enclose.controllers.constants.MyPluginConstants;
import com.ifpb.enclose.controllers.project.CallsListProjectService;
import com.ifpb.visitor.filter.FilterClass;
import com.ifpb.visitor.filter.FilterMethod;
import com.intellij.psi.*;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CallsListTreeModelBrokers extends CallsListTreeModel implements TreeModel {
    private PsiElement rootElement;

    public CallsListTreeModelBrokers(CallsListProjectService component) {
        super(component);
    }

    @Override
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

            if (!myCallsListProjectService.isFilterBreakerOnes()) {
                list.add(expression);
                return;
            }

            Call c = PsiToCallConverter.getCallFrom(expression);
            List<Call> callFiltered = Arrays.asList(c).stream().filter(new FilterClass(MyPluginConstants.COLLECTION_CLASS_NAME).or(new FilterClass(MyPluginConstants.MAP_CLASS_NAME)).and(new FilterMethod())).collect(Collectors.toList());
            if (callFiltered.size() > 0)
                list.add(expression);
        }

        public List<PsiElement> getElements() {
            return list;
        }

    }
}
