package com.ifpb.window;

//import com.ifpb.enclose.view.CallsListPanel;
import com.ifpb.enclose.view.CallsListPanel;
import com.intellij.openapi.project.Project;
        import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;

        import javax.swing.*;

public class MyToolWindow {
    private JPanel panel1;
    private JButton clickMeButton;
    private Project project;

    public MyToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        clickMeButton.addActionListener((e) -> {
            doSomething();
        });

        this.doSomething();
    }

    private void doSomething() {
        Messages.showInfoMessage("Hello, World!", "Hello World Message!");
        //panel1 = new CallsListPanel(project);
    }

    public JPanel getContent() {
        return this.panel1;
    }
}
