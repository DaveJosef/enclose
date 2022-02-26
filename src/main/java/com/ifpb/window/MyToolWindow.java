package com.ifpb.window;

import com.ifpb.actions.ListCallsAction;
import com.ifpb.calls.CallList;
//import com.ifpb.view.CallsListPanel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;

public class MyToolWindow {
    private Tree tree1;
    private JPanel panel1;
    private JButton clickMeButton;

    public MyToolWindow(ToolWindow toolWindow) {
        clickMeButton.addActionListener((e) -> {
            Messages.showInfoMessage("Hello, World!", "Hello World Message!");
        });

        this.doSomething();
    }

    private void doSomething() {
        System.out.println("Hello, World!");
        JPanel panel = new JPanel(new HorizontalLayout(0));

    }

    public JPanel getContent() {
        return this.panel1;
    }
}
