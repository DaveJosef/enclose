package com.ifpb.enclose;

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


public class RefactorIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {

        final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final CodeStyleManager codeStylist = CodeStyleManager.getInstance(project);

        //PsiClass classeAlvo = PsiTreeUtil.getParentOfType(factory.createTypeElementFromText("A", null).getReference().resolve(), PsiClass.class);
        PsiClass classeAlvo = PsiShortNamesCache.getInstance(project).getClassesByName("A", GlobalSearchScope.allScope(project))[0];

        //Criando a nova chamada para substituir a que quebra o confinamento;
        //Criando a estrutura basica do novo metodo em A;
        //Criando o return statement desse novo metodo;
        //Criando também o novo argumento que vai estar na expressão do novo método em A;
        //Por último, criando o novo "chamador".
        PsiExpressionStatement newCallStatement = (PsiExpressionStatement) factory.createStatementFromText("a.yourNewMethod(new A());", null);
        PsiMethod newMethod = factory.createMethodFromText("public boolean yourNewMethod(A YourNewParameter1) {}", null);
        PsiReturnStatement newReturnStatement = (PsiReturnStatement) factory.createStatementFromText("return ;", null);
        PsiExpression novoParametro = factory.createExpressionFromText("YourNewParameter1", null);
        PsiExpression novoQuemChama = factory.createExpressionFromText("this", null);


        PsiExpressionStatement oldStatement = PsiTreeUtil.getParentOfType(element, PsiExpressionStatement.class);

        PsiMethodCallExpression oldExpression = (PsiMethodCallExpression) oldStatement.getExpression();

        PsiMethodCallExpression percorredor = oldExpression;
        while (!(percorredor.getMethodExpression().getQualifierExpression() instanceof PsiReferenceExpression)) {
            percorredor = (PsiMethodCallExpression) percorredor.getMethodExpression().getQualifierExpression();
        }
        PsiReferenceExpression quemChama = (PsiReferenceExpression) percorredor.getMethodExpression().getQualifierExpression();

        PsiExpression argumento = oldExpression.getArgumentList().getExpressions()[0];

        // Refatorando na classe A
        quemChama.replace(novoQuemChama);
        argumento.replace(novoParametro);

        newReturnStatement.addBefore(oldExpression, newReturnStatement.getLastChild());

        newMethod.addBefore(newReturnStatement, newMethod.getBody().getLastChild());

        classeAlvo.addBefore(newMethod, classeAlvo.getLastChild());

        // formatando a classe A
        classeAlvo = PsiShortNamesCache.getInstance(project).getClassesByName("A", GlobalSearchScope.allScope(project))[0];
        classeAlvo = (PsiClass) codeStylist.reformat(classeAlvo);

        // Refatorando na classe C
        oldStatement.replace(newCallStatement);

        // formatando a classe C
        PsiClass clientClass = PsiTreeUtil.getParentOfType(newCallStatement, PsiMethod.class).getContainingClass();
        clientClass = (PsiClass) codeStylist.reformat(clientClass);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element == null) {
            return false;
        }
        if (PsiTreeUtil.getParentOfType(element, PsiExpressionStatement.class).getText().equals("a.getElements().add(new A());")) {
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
}
