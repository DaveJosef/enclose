package com.ifpb.actions;

import com.ifpb.visitor.PrintMethodCallVisitor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import icons.MyPluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ListCallsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project projeto = e.getProject();

        // --- COM PROCESSOR
        //JavaClassesProcessor processor = new JavaClassesProcessor();
        //AllClassesGetter.processJavaClasses(new PlainPrefixMatcher(""), projeto, GlobalSearchScope.projectScope(projeto), processor);

        // --- COM VISITOR
        Module[] modulosDoProjeto = ModuleManager.getInstance(projeto).getModules();
        VirtualFile[] virtualFileSrc = ModuleRootManager.getInstance(modulosDoProjeto[0]).getSourceRoots();
        PsiDirectory srcDir = PsiManager.getInstance(projeto).findDirectory(virtualFileSrc[0]);

        PrintMethodCallVisitor visitor = new PrintMethodCallVisitor();
        srcDir.accept(visitor);

        //String m = "Olá, Mundo!";
        String m = visitor.visitToString();
        //String m = processor.processToString();
        String t = "Chamadas de metodos encontradas em " + srcDir.getName() + ":";
        Icon icon = MyPluginIcons.ListCallsAction; // Créditos da Imagem: https://www.onlinewebfonts.com/icon/448372

        Messages.showMessageDialog(m, t, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project p = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(p != null);
        e.getPresentation().setEnabledAndVisible(editor != null);
        e.getPresentation().setIcon(MyPluginIcons.ListCallsAction);
    }
}
