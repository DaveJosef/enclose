package com.ifpb.experiment.actions;

import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.experiment.SetupDirectories;
import com.ifpb.visitor.MethodCallVisitor;
import com.ifpb.visitor.filter.FilterClass;
import com.ifpb.visitor.filter.FilterMethod;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import icons.MyPluginIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Collectors;

public class ExtractSamplesData extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project projeto = e.getProject();
        DefaultDataExtractor extractor = new DefaultDataExtractor(projeto);

        String t = "Chamadas de metodos encontradas em " + projeto.getBasePath() + ":";
        Icon icon = MyPluginIcons.ListCallsAction;

        extractor.extractOfAll();
//        Path path = Paths.get(SetupDirectories.PROJETO1);
//        VirtualFile pastaDoProjeto = VfsUtil.findFile(path, true);
        Messages.showMessageDialog("Pronto!", t, icon);
    }

}
