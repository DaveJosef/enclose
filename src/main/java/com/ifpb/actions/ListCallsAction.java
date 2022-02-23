package com.ifpb.actions;

import com.ifpb.visitor.MethodCallVisitor;
import com.ifpb.visitor.PrintMethodCallVisitor;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import icons.MyPluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ListCallsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project projeto = e.getProject();

        // --- COM PROCESSOR
        //JavaClassesProcessor processor = new JavaClassesProcessor();
        //AllClassesGetter.processJavaClasses(new PlainPrefixMatcher(""), projeto, GlobalSearchScope.projectScope(projeto), processor);

        // --- COM VISITOR
        MethodCallVisitor visitor = new MethodCallVisitor();
        Path path = Paths.get(projeto.getBasePath());
        VirtualFile pastaDoProjeto = VfsUtil.findFile(path, true);
        PsiDirectory dir = PsiManager.getInstance(projeto).findDirectory(pastaDoProjeto);
        if (dir != null) dir.accept(visitor);

        //String m = "Olá, Mundo!";
        String m = visitor.toString();
        //String m = processor.processToString();
        String t = "Chamadas de metodos encontradas em " + path + ":";
        Icon icon = MyPluginIcons.ListCallsAction; // Créditos da Imagem: https://www.onlinewebfonts.com/icon/448372

        Messages.showMessageDialog(m, t, icon);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project p = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(p != null);
        //e.getPresentation().setEnabledAndVisible(editor != null);
        e.getPresentation().setIcon(MyPluginIcons.ListCallsAction);
    }
}
