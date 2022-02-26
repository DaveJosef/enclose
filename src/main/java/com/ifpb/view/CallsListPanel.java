/*
package com.ifpb.view;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class CallsListPanel extends JPanel {
    private static final Logger LOG = Logger.getInstance(CallsListPanel.class);

    private String _actionTitle;
    private CallsListTree _tree;
    private CallsListTreeModel _model;
    private PsiElement _rootElement;
    private PsiElement _selectedElement;

    private final Project _project;
    private ToolWindow _toolwindow;

    public CallsListPanel(CallsListProjectService _projectComponent) {
        this._projectComponent = _projectComponent;
        _project = _projectComponent.getProject();
        _caretMover = new EditorCaretMover(_projectComponent.getProject);
        _highlighter = new EditorPsiElementHighlighter(_project);
        _model = new CallsListTreeModel(_projectComponent);
        _treeSelectListener = new ViewerTreeSelectionListener();

        buildGUI();
    }

    private void buildGUI() {
        setLayout(new BorderLayout());

        _tree = new CallsListTree(_model);
        _tree.getSelectionModel().addTreeSelectionListener(_treeSelectListener);

        InputMap inputMap = _tree.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, true), "EditSource");
    }

    private void changeTreeSelection() {
        TreePath path = getPath(getSelectedElement);
        _tree.expandPath(path);
        _tree.scrollPathToVisible(path);
        _tree.setSelectionPath(path);
    }

    private TreePath getPath(PsiElement element) {
        if (element == null) return null;
        LinkedList list = new LinkedList();
        while (element != null && element != _rootElement) {
            list.addFirst(element);
            element = element.getParent();
        }
        if (element != null)
            list.addFirst(element);
        TreePath treePath = new TreePath(list.toArray());
        debug("root=" + _rootElement + ", treePath=" + treePath);
    }

    private void moveEditorCaret() {
        if (_projectComponent.isAutoScrollToSource()) {
            LOG.debug("moving editor caret");
            _caretMover.modeEditorCaret(getSelectedElement())
        }
    }

    private void setRootElement(PsiElement rootElement) {
        if (rootElement instanceof PsiFile) {
            FileViewProvider viewProvider = ((PsiFile) rootElement).getViewProvider();

            _projectComponent.updateLanguageList(ContainerUtil);
        }
    }
}
*/
