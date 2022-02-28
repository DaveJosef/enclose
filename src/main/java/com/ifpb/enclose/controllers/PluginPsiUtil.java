package com.ifpb.enclose.controllers;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class PluginPsiUtil {
    public static VirtualFile getVirtualFile(PsiElement element) {
        if (element == null || !element.isValid() || element.getContainingFile() == null)
            return null;
        return element.getContainingFile().getVirtualFile();
    }

    public static boolean isElementInSelectedFile(Project project, PsiElement element) {
        VirtualFile elementFile = getVirtualFile(element);
        if (elementFile == null)
            return false;

        VirtualFile[] currentEditedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        for (VirtualFile file : currentEditedFiles) {
            if (elementFile.equals(file)) {
                return true;
            }
        }
        return false;
    }

    public static Editor getEditorIfSelected(Project project, PsiElement element) {
        VirtualFile elementFile = getVirtualFile(element);
        if (elementFile == null)
            return null;

        FileEditor fileEditor = FileEditorManager.getInstance(project).getSelectedEditor(elementFile);

        Editor editor = null;
        if (fileEditor instanceof TextEditor) {
            editor = ((TextEditor) fileEditor).getEditor();
        }
        return editor;
    }

    public static PsiFile getContainingFile(PsiElement selectedElement) {
        if (selectedElement == null || !selectedElement.isValid())
            return null;

        return selectedElement.getContainingFile();
    }
}
