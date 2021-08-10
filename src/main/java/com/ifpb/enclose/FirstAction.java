package com.ifpb.enclose;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class FirstAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project actualProject = e.getProject();

        // final message:
        Messages.showMessageDialog(actualProject,
                "Hello, World!",
                "Enclose",
                Messages.getInformationIcon());
    }
}
