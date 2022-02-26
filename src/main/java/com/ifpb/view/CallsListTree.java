package com.ifpb.view;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

public class CallsListTree extends JTree {
    public CallsListTree(TreeModel newModel) {
        super(newModel);
        setCellRenderer(new CallsListTreeCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setExpandsSelectedPaths(true);
    }
}
