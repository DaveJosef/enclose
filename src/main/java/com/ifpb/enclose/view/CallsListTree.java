package com.ifpb.enclose.view;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.*;

public class CallsListTree extends Tree {
    public CallsListTree(TreeModel newModel) {
        super(newModel);
        setCellRenderer(new CallsListTreeCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setExpandsSelectedPaths(true);
    }

}
