package com.ifpb.enclose;

import com.ifpb.enclose.Call;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RefactorIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    private CallList calllist = new CallList();
    private Call chosenCall;
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);

        // Realizando a coleta:
        String NEW_METHOD_NAME = "yourNewestMethod";
        String NEW_PARAMETER_NAME = "yourNewestParameter";
        PsiClass classeAlvo = PsiShortNamesCache.getInstance(project).getClassesByName(chosenCall.trgtClass(), GlobalSearchScope.allScope(project))[0];
        PsiClass classeCliente = PsiShortNamesCache.getInstance(project).getClassesByName(chosenCall.clientClass(), GlobalSearchScope.allScope(project))[0];
        PsiMethodCallExpression chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        while (PsiTreeUtil.getParentOfType(chamada, PsiMethodCallExpression.class) instanceof PsiMethodCallExpression) {
            chamada = PsiTreeUtil.getParentOfType(chamada, PsiMethodCallExpression.class);
        }
        PsiMethodCallExpression novaChamadaCliente = (PsiMethodCallExpression) factory.createExpressionFromText("x." + NEW_METHOD_NAME + "()", null);
        PsiMethod novoMetodo = factory.createMethodFromText("public " + NEW_METHOD_NAME + "() " + "{}", null);
        PsiReturnStatement novoReturn = (PsiReturnStatement) factory.createStatementFromText("return ;", null);
        PsiType tipoRetorno = chamada.getType();
        PsiMethodCallExpression novaChamada = (PsiMethodCallExpression) factory.createExpressionFromText(chamada.getText(), null);
        List<PsiExpression> listaDeExpressoes = new ArrayList<>();
        List<PsiType> listaDeNovosTipos = new ArrayList<>();
        List<PsiParameter> listaDeNovosParametros = new ArrayList<>();
        List<PsiReferenceExpression> listaDeNovasReferencias = new ArrayList<>();
        PsiMethodCallExpression percorredor = chamada;
        int n=0;
        while (!(percorredor.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            if (percorredor.getArgumentList().getExpressions() != null) {
                for (PsiExpression expr : percorredor.getArgumentList().getExpressions()) {
                    n++;
                    listaDeExpressoes.add(expr);
                    listaDeNovosParametros.add(factory.createParameterFromText(NEW_PARAMETER_NAME + n, null));
                    listaDeNovosTipos.add(factory.createTypeFromText(expr.getType().getCanonicalText(), null));
                    listaDeNovasReferencias.add((PsiReferenceExpression) factory.createExpressionFromText(NEW_PARAMETER_NAME + n, null));
                }
            }
            percorredor = (PsiMethodCallExpression) percorredor.getMethodExpression().getQualifierExpression();
        }
        n=0;

        // refatorando classe A:
        percorredor = novaChamada;
        n=0;
        while (!(percorredor.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            if (percorredor.getArgumentList().getExpressions() != null) {
                for (PsiExpression expr : percorredor.getArgumentList().getExpressions()) {
                    expr.replace(listaDeNovasReferencias.get(n));
                    n++;
                }
            }
            percorredor = (PsiMethodCallExpression) percorredor.getMethodExpression().getQualifierExpression();
        }
        n=0;
        novoReturn.addBefore(novaChamada, novoReturn.getLastChild());
        novoMetodo.addAfter((PsiTypeElement) tipoRetorno, novoMetodo.getModifierList());
        novoMetodo.addBefore(novoReturn, novoMetodo.getBody().getLastChild());
        n = 0;
        for (PsiParameter param : listaDeNovosParametros) {
            PsiType novoTipo = listaDeNovosTipos.get(n);
            param.addBefore((PsiTypeElement) novoTipo, param.getLastChild());
            novoMetodo.addBefore(param, novoMetodo.getParameterList().getLastChild());
            n++;
        }
        n = 0;
        classeAlvo.addBefore(novoMetodo, classeAlvo.getLastChild());
        classeAlvo = (PsiClass) codeStylist.reformat(classeAlvo);

        // refatorando em C:
        percorredor = chamada;
        while (!(percorredor.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            percorredor = (PsiMethodCallExpression) percorredor.getMethodExpression().getQualifierExpression();
        }
        for (PsiExpression expr : listaDeExpressoes) {
            novaChamadaCliente.addBefore(expr, novaChamadaCliente.getArgumentList().getLastChild());
        }
        novaChamadaCliente.getMethodExpression().getQualifierExpression().replace(percorredor.getMethodExpression().getQualifierExpression());
        chamada.replace(novaChamadaCliente);
        classeCliente = (PsiClass) codeStylist.reformat(classeCliente);

        // a.m1().m2().m3().m4();

    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element == null || PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class) == null) {
            return false;
        }

        PsiMethodCallExpression chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        while (PsiTreeUtil.getParentOfType(chamada, PsiMethodCallExpression.class) instanceof PsiMethodCallExpression) {
            for (Call call : calllist.calls()) {
                if (call.collectionMethod().equals(chamada.getMethodExpression().getLastChild().getText())) {
                    this.chosenCall = call;
                    return true;
                }
            }
            chamada = PsiTreeUtil.getParentOfType(chamada, PsiMethodCallExpression.class);
        }


        return false;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "RefactorIntention";
    }

    @NotNull
    public String getText() {
        return "Refactor violations of Law Of Demeter";
    }
}
