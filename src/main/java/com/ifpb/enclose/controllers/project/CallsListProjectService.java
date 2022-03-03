package com.ifpb.enclose.controllers.project;

import com.ifpb.enclose.controllers.actions.PropertyToggleAction;
import com.ifpb.enclose.view.CallsListPanel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.newui.HorizontalLayout;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import icons.MyPluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.*;
import java.util.Collections;

import static com.ifpb.enclose.controllers.constants.MyPluginConstants.*;

@State(name = "CallListProjectService", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class CallsListProjectService implements PersistentStateComponent<CallsListProjectService.State>, Disposable {
    @Override
    public void dispose() {

    }

    @Nullable
    @Override
    public State getState() {
        return currentState;
    }

    @Override
    public void loadState(@NotNull State state) {
        currentState = state;
    }

    static class State {
        public boolean FILTER_BREAKER_ONES = false;
    }
    private State currentState = new State();

    private CallsListPanel myListerPanel;
    //private EditorListener myEditorListener;
    private final Project myProject;

    public CallsListProjectService(Project myProject) {
        this.myProject = myProject;
    }

    public static CallsListProjectService getInstance(Project project) {
        return project.getService(CallsListProjectService.class);
    }

    public void registerToolWindow() {
        ToolWindow toolWindow = getToolWindow();
        initToolWindow(toolWindow);
        toolWindow.setAvailable(true, null);
    }

    private ToolWindow getToolWindow() {
        return ToolWindowManager.getInstance(myProject).getToolWindow(ID_TOOL_WINDOW);
    }

    public Project getMyProject() {
        return myProject;
    }

    public boolean isFilterBreakerOnes() {
        return currentState.FILTER_BREAKER_ONES;
    }

    public void setFilterBreakerOnes(boolean filterBreakerOnes) {
        currentState.FILTER_BREAKER_ONES = filterBreakerOnes;
        getListerPanel().applyBreakerFilter();
    }

    private CallsListPanel getListerPanel() {
        return myListerPanel;
    }

    public CallsListPanel initToolWindow(@NotNull ToolWindow toolWindow)
    {
        myListerPanel = new CallsListPanel(this);

        myListerPanel.addPropertyChangeListener("ancestor", it -> handleCurrentState());
        ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup actionGroup = new DefaultActionGroup(ID_ACTION_GROUP, false);
        actionGroup.add(new PropertyToggleAction("Filter Breaker Ones",
                "Select only the calls those break confinement.",
                MyPluginIcons.ListCallsAction,
                this::isFilterBreakerOnes,
                this::setFilterBreakerOnes));

        ActionToolbar toolBar = actionManager.createActionToolbar(ID_ACTION_TOOLBAR, actionGroup, false);

        JPanel panel = new JPanel(new HorizontalLayout(0));
        panel.add(toolBar.getComponent());

        myListerPanel.add(panel, BorderLayout.LINE_START);
        myListerPanel.setToolWindow(toolWindow);
        myListerPanel.loadProjectDirectory();

        //myEditorListener = new EditorListener(myViewerPanel, myProject);

        return myListerPanel;
    }

    private void handleCurrentState() {
        if (myListerPanel == null) return;

        if (myListerPanel.isDisplayable()) {
            //myEditorListener.start();
        } else {
            //myEditorListener.stop();
        }
    }

    public void unregisterToolWindow() {
        if (myListerPanel != null) {
            myListerPanel = null;
        }
/*
        if (myEditorListener != null) {
            myEditorListener.stop();
            myEditorListener = null;
        }
 */
        getToolWindow().setAvailable(false, null);
    }

}
