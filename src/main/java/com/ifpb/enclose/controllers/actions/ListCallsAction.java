package com.ifpb.enclose.controllers.actions;

import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.view.CallsListTree;
import com.ifpb.enclose.view.CallsListTreeModel;
import com.ifpb.visitor.MethodCallVisitor;
import com.ifpb.visitor.filter.FilterClass;
import com.ifpb.visitor.filter.FilterMethod;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import icons.MyPluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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

        CallList allCalls = new CallList(visitor.getVisitResult());
        CallList jcfCalls = new CallList(allCalls.calls().stream().filter(new FilterClass("java.util.Collection").or(new FilterClass("java.util.Map"))).collect(Collectors.toList()));
        CallList breakerOnes = new CallList(jcfCalls.calls().stream().filter(new FilterMethod()).collect(Collectors.toList()));

        //String m = "Olá, Mundo!";
        String m = "\nAll calls: ("+allCalls.calls().size()+")\n";
        m += allCalls.toString();
        m += "\nJCF: ("+jcfCalls.calls().size()+")\n";
        m += jcfCalls;
        m += "\nQUEBRAM O CONFINAMENTO: ("+breakerOnes.calls().size()+")\n";
        m += breakerOnes;
        //String m = processor.processToString();
        String t = "Chamadas de metodos encontradas em " + path + ":";
        Icon icon = MyPluginIcons.ListCallsAction; // Créditos da Imagem: https://www.onlinewebfonts.com/icon/448372

        //ToolWindowManager.getInstance(projeto).getToolWindow("Calls List").getContentManager().getFactory().createContent(new CallsListTree(new CallsListTreeModel(dir)), "Olá!", false);

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
