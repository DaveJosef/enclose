package com.ifpb.enclose;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.util.ArrayList;
import java.util.List;

public class PsiMethodCallExpressionUtils {
    public PsiMethodCallExpression getQualifierFromExpression(PsiMethodCallExpression expressaoPai, String nomeDoMetodoFim) {
        PsiMethodCallExpression m = expressaoPai;
        while (!(m.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            if (m.getMethodExpression().getReferenceName() == nomeDoMetodoFim) {
                return (PsiMethodCallExpression) m.getMethodExpression().getQualifierExpression();
            }
            m = (PsiMethodCallExpression) m.getMethodExpression().getQualifierExpression();
        }
        return expressaoPai;
    }
    public PsiExpressionList concatateExpressionLists(List<PsiExpressionList> listaDeExpressionLists, PsiExpressionList listaVazia) {
        PsiExpressionList lista = listaVazia;
        for (PsiExpressionList exprList : listaDeExpressionLists) {
            if (exprList != null) {
                for (PsiExpression expr : exprList.getExpressions()) {
                    lista.addBefore(expr, lista.getLastChild());
                }
            }
        }
        return lista;
    }
    public List<PsiExpressionList> getAllExpressionListsFromCall(PsiMethodCallExpression expressao) {
        PsiMethodCallExpression m = expressao;
        List<PsiExpressionList> lista = new ArrayList<>();
        while (!(m.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            lista.add(m.getArgumentList());
            m = (PsiMethodCallExpression) m.getMethodExpression().getQualifierExpression();
        }
        return lista;
    }
    public void replaceArgumentsWithReferences(PsiMethodCallExpression expressao, List<PsiReferenceExpression> listaDeReferencias) {
        PsiMethodCallExpression m = expressao;
        int n = 0;
        while (!(m.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            if (m.getArgumentList().getExpressions() != null && listaDeReferencias.get(n) != null) {
                for (PsiExpression expression : m.getArgumentList().getExpressions()) {
                    expression.replace(listaDeReferencias.get(n));
                    n++;
                }
            }
            m = (PsiMethodCallExpression) m.getMethodExpression().getQualifierExpression();
        }
    }
    public PsiParameterList generateNewParameterList(int size, PsiParameterList listaVazia, Project project, String nameTemplate) {
        PsiParameterList lista = listaVazia;
        int n = 0;
        for (n = 0; n < size; n ++) {
            PsiParameter novoParametro = JavaPsiFacade.getInstance(project).getElementFactory().createParameterFromText(nameTemplate + n, null);
            lista.addBefore(novoParametro, lista.getLastChild());
        }
        return (PsiParameterList) CodeStyleManager.getInstance(project).reformat(lista);
    }
    public List<PsiReferenceExpression> generateNewReferenceExpressionList(int size, Project project, String nameTemplate) {
        List<PsiReferenceExpression> lista = new ArrayList<>();
        int n = 0;
        for (n = 0; n < size; n ++) {
            PsiReferenceExpression novaReferencia = (PsiReferenceExpression) JavaPsiFacade.getInstance(project).getElementFactory().createExpressionFromText(nameTemplate + n, null);
            lista.add(novaReferencia);
        }
        return lista;
    }
    /*public List<PsiType> extractTypesFromCall(PsiMethodCallExpression expressao) {
        List<PsiType> lista = new ArrayList<>();
        PsiMethodCallExpression m = expressao;
        while (!(m.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            lista.add(m.getArgumentList().getExpressions()[n]);
            m = (PsiMethodCallExpression) m.getMethodExpression().getQualifierExpression();
        }
        return lista;
    }*/
    public PsiParameterList addTypesToParameters(List<PsiType> listaDeTipos, PsiParameterList listaDeParametros, Project project) {
        PsiParameterList lista = listaDeParametros;
        PsiTypeElement novoElementoTipo;
        int n = 0;
        for (PsiParameter p : lista.getParameters()) {
            novoElementoTipo = JavaPsiFacade.getInstance(project).getElementFactory().createTypeElement(listaDeTipos.get(n));
            p.addBefore(novoElementoTipo, p.getLastChild());
            n++;
        }
        return lista;
    }
}
