
package com.ifpb.enclose.view;

import com.ifpb.enclose.controllers.EditorCaretMover;
import com.ifpb.enclose.controllers.calls.CallList;
import com.ifpb.visitor.MethodCallVisitor;
import com.intellij.ide.plugins.newui.VerticalLayout;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.*;
import com.intellij.ui.components.panels.HorizontalLayout;
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

    private String _actionTitle;
    private CallsListTree _tree;
    private CallsListTreeModel _model;
    private PsiElement _rootElement;
    private PsiElement _selectedElement;
    private final Project _project;
    private ToolWindow _toolwindow;
    private final EditorCaretMover _caretMover;

    public CallsListPanel(Project project) {
        _project = project;
        _caretMover = new EditorCaretMover(project);
        Path path = Paths.get(project.getBasePath());
        VirtualFile pastaDoProjeto = VfsUtil.findFile(path, true);
        PsiDirectory dir = PsiManager.getInstance(project).findDirectory(pastaDoProjeto);
        //_model = new CallsListTreeModel(JavaPsiFacade.getElementFactory(project).createMethodFromText("public void m() {return this;}", null));
        _model = new CallsListTreeModel(dir);

        buildGUI();
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
        add(_tree);
    }

    private void moveEditorCaret() {
        _caretMover.moveEditorCaret(getSelectedElement());
    }

    private void setSelectedElement(PsiElement element) {
        _selectedElement = element;
        moveEditorCaret();
    }

    private PsiElement getSelectedElement() {
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

    private void resetTree() {
        Enumeration expandedDescendants = null;
        TreePath treePath = null;
        if (_model.getRoot() != null) {
            expandedDescendants = _tree.getExpandedDescendants(new TreePath(_model.getRoot()));
            treePath = _tree.getSelectionModel().getSelectionPath();
        }

        _model = new CallsListTreeModel(_rootElement);
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
}
