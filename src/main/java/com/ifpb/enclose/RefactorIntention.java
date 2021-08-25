package com.ifpb.enclose;

//import com.ifpb.enclose.Call;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class RefactorIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    private CallList calllist = new CallList();
    private Call chosenCall = new Call();
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);
        final PsiMethodCallExpressionUtils util = new PsiMethodCallExpressionUtils();
        final PsiShortNamesCache cache = PsiShortNamesCache.getInstance(project);

        PsiClass classeAlvo = cache.getClassesByName("A", GlobalSearchScope.allScope(project))[0];
        PsiClass classeCliente = cache.getClassesByName("C", GlobalSearchScope.allScope(project))[0];

        /*
         * coletar os argumentos da chamada velha
         * */
        List<PsiExpression> listaDeArgumentosDaChamadaVelha = new ArrayList<>();
        PsiMethodCallExpression chamadavelha = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        PsiMethodCallExpression p = chamadavelha;
        while (!(p.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            PsiExpressionList lista = p.getArgumentList();
            if (!(lista.isEmpty())) {
                for (PsiExpression e : lista.getExpressions()) {
                    listaDeArgumentosDaChamadaVelha.add(e);
                }
            }
            p = (PsiMethodCallExpression) p.getMethodExpression().getQualifierExpression();
        }
        PsiExpressionList lista = p.getArgumentList();
        if (!(lista.isEmpty())) {
            for (PsiExpression e : lista.getExpressions()) {
                listaDeArgumentosDaChamadaVelha.add(e);
            }
        }

        /*
         * adicionar os parâmetros à lista de parâmetros
         * */
        int n = 0;
        String stringdeparametros = "";
        for (PsiExpression e : listaDeArgumentosDaChamadaVelha) {
            n++;
            stringdeparametros += e.getType().getCanonicalText() + " " + "param" + n + (listaDeArgumentosDaChamadaVelha.get(listaDeArgumentosDaChamadaVelha.size() - 1).equals(e) ? "" : ", ");
        }
        Messages.showMessageDialog(stringdeparametros, "stringdepararmetros", null);

        /*
         * adicionar o tipo de retorno
         * */

        /*
         * pegar a expressao do meio entre o qualifier (inicio) e o metodo pai
         * */

        /*
         * colocar todos os argumentos nela
         * */

        /*
         * adicionar o this à expressão
         * */
        p = (PsiMethodCallExpression) factory.createExpressionFromText(chamadavelha.getText(), null);
        PsiMethodCallExpression chamadaintermediaria = p;
        PsiExpression qualifierdaexpressaoema = chamadaintermediaria.getMethodExpression().getQualifierExpression();
        boolean retorne = false;
        while (!(p.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression) && retorne == false) {
            if (p.getMethodExpression().getReferenceName().equals("add")) {
                retorne = true;
                chamadaintermediaria = p;
                PsiMethodCallExpression o = chamadaintermediaria;
                while (!(o.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
                    if (o.getMethodExpression().getReferenceName().equals("getElements")) {
                        qualifierdaexpressaoema = o.getMethodExpression().getQualifierExpression();
                    }
                    o = (PsiMethodCallExpression) o.getMethodExpression().getQualifierExpression();
                }
                if (o.getMethodExpression().getReferenceName().equals("getElements")) {
                    qualifierdaexpressaoema = o.getMethodExpression().getQualifierExpression();
                }
            } else {
                p = (PsiMethodCallExpression) p.getMethodExpression().getQualifierExpression();
            }
        }
        if (p.getMethodExpression().getReferenceName().equals("add")) {
            chamadaintermediaria = p;
        } else {
            p = (PsiMethodCallExpression) p.getMethodExpression().getQualifierExpression();
        }
        PsiThisExpression thisqualifier = (PsiThisExpression) factory.createExpressionFromText("this", null);
        qualifierdaexpressaoema.replace(thisqualifier);
        Messages.showMessageDialog(qualifierdaexpressaoema.getText(), "qualifierdaexpressaoema", null);

        /*
         * substituir cada argumento por uma referência ao parâmetro
         * */
        int a = 0;
        PsiMethodCallExpression r = chamadaintermediaria;
        while (!(r.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            if (!(r.getArgumentList().isEmpty())) {
                for (PsiExpression e : r.getArgumentList().getExpressions()) {
                    a++;
                    String nome = "param" + n;
                    e.replace((PsiReferenceExpression) factory.createExpressionFromText(nome, null));
                }
            }
            r = (PsiMethodCallExpression) r.getMethodExpression().getQualifierExpression();
        }
        if (!(r.getArgumentList().isEmpty())) {
            for (PsiExpression e : r.getArgumentList().getExpressions()) {
                a++;
                String nome = "param" + n;
                e.replace((PsiReferenceExpression) factory.createExpressionFromText(nome, null));
            }
        }
        Messages.showMessageDialog(chamadaintermediaria.getText(), "chamadaintermediaria", null);

        /*
         * adicionar a expressão ao return
         * */
        PsiStatement novoretorno = factory.createStatementFromText("return + " + chamadaintermediaria.getText() + ";", null);

        /*
         * adicionar o return
         * */

        /*
         * criar o novo método
         * */
        PsiType tipoderetorno = chamadaintermediaria.getType();
        PsiMethod novometodo = factory.createMethodFromText("public " + tipoderetorno.getCanonicalText() + " method(" + stringdeparametros + ") { " + novoretorno.getText() + " }", null);
        classeAlvo.addBefore(novometodo, classeAlvo.getLastChild());
        Messages.showMessageDialog(novoretorno.getText(), "novoretorno", null);

        /*
         * substituir a chamada antiga em C, por uma nova
         * */
        PsiMethodCallExpression q = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        while(!(q.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            q = (PsiMethodCallExpression) q.getMethodExpression().getQualifierExpression();
        }

        PsiMethodCallExpression chamadanovaemc = (PsiMethodCallExpression) factory.createExpressionFromText(q.getMethodExpression().getQualifierExpression().getText() + ".method()", null);
        for (PsiExpression e : listaDeArgumentosDaChamadaVelha) {
            chamadanovaemc.getArgumentList().add(e);
        }
        chamadavelha.replace(chamadanovaemc);

    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element == null) {
            return false;
        }
        PsiMethodCallExpression chamada = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression.class);
        if (chamada == null) {
            return false;
        }

        if (chamada.getText().contains("." + chosenCall.trgtMethod() + "(") && chamada.getText().contains("." + chosenCall.collectionMethod() + "(")) {
            return true;
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

    public boolean startInWriteAction() {
        return true;
    }
}
