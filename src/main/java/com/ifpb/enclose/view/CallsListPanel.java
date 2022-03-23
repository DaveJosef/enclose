
package com.ifpb.enclose.view;

import com.ifpb.enclose.controllers.EditorCaretMover;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.enclose.controllers.project.CallsListProjectService;
import com.ifpb.visitor.MethodCallVisitor;
import com.intellij.ide.plugins.newui.VerticalLayout;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.*;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.uiDesigner.compiler.ScrollPaneLayoutCodeGenerator;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;

public class CallsListPanel extends JPanel {
    private static final Logger LOG = Logger.getInstance(CallsListPanel.class);
    private final CallsListProjectService _projectComponent;
    private String _actionTitle;
    private Tree _tree;
    private CallsListTreeModel _model;
    private PsiElement _rootElement;
    private PsiElement _selectedElement;
    private final Project _project;
    private ToolWindow _toolwindow;
    private final EditorCaretMover _caretMover;

    public CallsListPanel(CallsListProjectService component) {
        _projectComponent = component;
        _project = component.getMyProject();
        _caretMover = new EditorCaretMover(component.getMyProject());
        _model = new CallsListTreeModelBrokers(component);

        buildGUI();
    }

    public void loadProjectDirectory() {

        Path path = Paths.get(_projectComponent.getMyProject().getBasePath());
        VirtualFile pastaDoProjeto = VfsUtil.findFile(path, true);
        PsiDirectory dir = PsiManager.getInstance(_projectComponent.getMyProject()).findDirectory(pastaDoProjeto);
        //_model = new CallsListTreeModelBrokers(JavaPsiFacade.getElementFactory(_projectComponent.getMyProject()).createMethodFromText("public void m() {return this;}", null));
        setRootElement(dir);
    }

    private void buildGUI() {
        setLayout(new BorderLayout());

        _tree = new CallsListTree(_model);

        _tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                setSelectedElement((PsiElement) _tree.getLastSelectedPathComponent());
                System.out.println(getSelectedElement());
            }
        });

        _tree.getActionMap().put("EditSource", new AbstractAction("EditSource") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (getSelectedElement() == null) return;
                Editor editor = _caretMover.openInEditor(getSelectedElement());
                //selectElementAtCaret(editor, "Tree selection changed");
                editor.getContentComponent().requestFocus();
            }
        });

        //add(_list);
        add(new JBScrollPane(_tree));
    }

    private void moveEditorCaret() {
        _caretMover.moveEditorCaret(getSelectedElement());
    }

    private void setSelectedElement(PsiElement element) {
        _selectedElement = element;
        _projectComponent.setRefactoringElement(getSelectedElement());
        moveEditorCaret();
    }

    public PsiElement getSelectedElement() {
        return _selectedElement;
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
        return treePath;
    }

    private void refreshRootElement() {
        selectRootElement(getRootElement());
    }

    private void selectRootElement(PsiElement element) {
        setRootElement(element);
    }

    private void setRootElement(PsiElement rootElement) {
        _rootElement = rootElement;
        showRootElement();
    }

    private void showRootElement() {
        getToolWindow().setTitle("Mostrando para raiz: " + _rootElement);
        resetTree();
    }

    private ToolWindow getToolWindow() {
        return _toolwindow;
    }

    public PsiElement getRootElement() {
        return _rootElement;
    }

    private void resetTree() {
        Enumeration expandedDescendants = null;
        TreePath treePath = null;
        if (_model.getRoot() != null) {
            expandedDescendants = _tree.getExpandedDescendants(new TreePath(_model.getRoot()));
            treePath = _tree.getSelectionModel().getSelectionPath();
        }

        _model = new CallsListTreeModelBrokers(_projectComponent);
        _model.setRootElement(getRootElement());
        _tree.setModel(_model);
        if (expandedDescendants != null) {
            while (expandedDescendants.hasMoreElements()) {
                TreePath treePath1 = (TreePath) expandedDescendants.nextElement();
                _tree.expandPath(treePath1);
            }
        }
        _tree.setSelectionPath(treePath);
        _tree.scrollPathToVisible(treePath);
    }

    public static void debug(String m) {
        if (LOG.isDebugEnabled()) LOG.debug(m);
    }

    public void applyBreakerFilter() {
        showRootElement();
    }

    public void setToolWindow(ToolWindow toolWindow) {
        _toolwindow = toolWindow;
    }
}
