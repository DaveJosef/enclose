package com.ifpb.window;

import com.ifpb.enclose.controllers.project.CallsListProjectService;
import com.ifpb.enclose.view.CallsListPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class MyToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CallsListProjectService component = CallsListProjectService.getInstance(project);
        ContentManager contentManager = toolWindow.getContentManager();
        CallsListPanel panel = component.initToolWindow(toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, null, false);
        contentManager.addContent(content);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return !project.isDefault();
    }
}
