package com.ifpb.calls;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PsiToCallConverter {

    public static CallMethodElement getCallMethodElementFrom(PsiMethodCallExpression expression) {
        String returnType = null;
        List<String> params = new ArrayList<>();
        String methodName = null;

        PsiType refType = expression.getType();
        if (refType != null) returnType = refType.getCanonicalText();

        PsiExpressionList refParamList = expression.getArgumentList();
        PsiType[] refParamListTypes = null;
        if (refParamList != null) refParamListTypes = refParamList.getExpressionTypes();
        if (refParamListTypes != null) Arrays.stream(refParamListTypes).forEach(refParamListType -> params.add(refParamListType.getCanonicalText()));

        methodName = expression.getMethodExpression().getReferenceName();

        return new CallMethodElement(returnType, params, methodName);
    }

    public static CallMethodElement getCallMethodElementFrom(PsiExpression expression) {
        if (expression instanceof PsiMethodCallExpression)
            return getCallMethodElementFrom((PsiMethodCallExpression) expression);

        String returnType = null;
        List<String> params = new ArrayList<>();
        String methodName = null;

        if (expression instanceof PsiReferenceExpression) {
            if (expression.getType() != null) returnType = expression.getType().getCanonicalText();
            methodName = ((PsiReferenceExpression) expression).getReferenceName();
        }

        return new CallMethodElement(returnType, params, methodName);
    }

    public static CallMethodElement getCallMethodElementFrom(PsiMethod m) {
        String returnType = null;
        List<String> params = new ArrayList<>();
        String methodName = null;

        PsiType refType = m.getReturnType();
        if (refType != null) returnType = refType.getCanonicalText();

        PsiParameter[] refParamList = m.getParameterList().getParameters();
        List<PsiType> refParamListTypes = new ArrayList<>();
        if (refParamList != null) Arrays.stream(refParamList).forEach(refParam -> refParamListTypes.add(refParam.getType()));
        if (refParamListTypes != null) refParamListTypes.forEach(refParamListType -> params.add(refParamListType.getCanonicalText()));

        methodName = m.getName();

        return new CallMethodElement(returnType, params, methodName);
    }

    public static Call getCallFrom(PsiMethodCallExpression expression) {
        Call currentCall = new Call();

        String targetClass = null;
        String targetMethod = null;
        String clientClass = null;
        String clientMethod = null;
        String collectionMethod = null;

        // Navigating the Psi
        PsiReferenceExpression methodExpr = expression.getMethodExpression();

        collectionMethod = methodExpr.getReferenceName();
        currentCall.setCollectionMethod(getCallMethodElementFrom(expression));

        // Client Class
        PsiClass clientClassPsi = PsiTreeUtil.getParentOfType(methodExpr, PsiClass.class);
        clientClass = clientClassPsi.getQualifiedName();
        currentCall.setClientClass(clientClass);

        // Client Method
        PsiMethod clientMethodPsi = PsiTreeUtil.getParentOfType(methodExpr, PsiMethod.class);
        if (clientMethodPsi != null)
            currentCall.setClientMethod(getCallMethodElementFrom(clientMethodPsi));

        if (!(methodExpr.getQualifierExpression() instanceof PsiMethodCallExpression)) {
            return currentCall;
        }
        PsiReferenceExpression qualiExpr = ((PsiMethodCallExpression) methodExpr.getQualifierExpression()).getMethodExpression();
        if (qualiExpr == null) {
            return currentCall;
        }

        PsiReferenceExpression qualiExpr2 = null;
        if (qualiExpr.getQualifierExpression() instanceof PsiMethodCallExpression) {
            qualiExpr2 = ((PsiMethodCallExpression) qualiExpr.getQualifierExpression()).getMethodExpression();
        } else if (qualiExpr.getQualifierExpression() instanceof PsiReferenceExpression) qualiExpr2 = (PsiReferenceExpression) qualiExpr.getQualifierExpression();
        if (qualiExpr2 == null) {
            return currentCall;
        }

        // Target Method
        currentCall.setTargetMethod(getCallMethodElementFrom(expression.getMethodExpression().getQualifierExpression()));

        // Target Class
        PsiType qualiExpr2Type = qualiExpr2.getType();
        if (qualiExpr2Type == null) {
            return currentCall;
        }
        targetClass = qualiExpr2Type.getCanonicalText();
        currentCall.setTargetClass(targetClass);
        //

        return currentCall;
    }
}
