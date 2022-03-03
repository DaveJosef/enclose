package com.ifpb.enclose.controllers;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

// Retirado de https://github.com/cmf/psiviewer/blob/master/src/idea/plugin/psiviewer/view/EditorCaretMover.java
public class EditorCaretMover {
    private final Project project;
    private boolean _shouldMoveCaret = true;

    public EditorCaretMover(Project project) {
        this.project = project;
    }

    private Editor getEditor(PsiElement element) {
        return PluginPsiUtil.getEditorIfSelected(project, element);
    }

    public void moveEditorCaret(PsiElement element) {
        if (element == null) return;
        if (!PluginPsiUtil.isElementInSelectedFile(project, element)) openInEditor(element);
        if (shouldMoveCaret(element))
        {
            Editor editor = getEditor(element);
            if (editor == null) return;

            int textOffset = element.getTextOffset();
            if (textOffset < editor.getDocument().getTextLength())
            {
                editor.getCaretModel().moveToOffset(textOffset);
                editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
            }
        }
        _shouldMoveCaret = true;
    }

    private boolean shouldMoveCaret(PsiElement element) {
        return _shouldMoveCaret && PluginPsiUtil.isElementInSelectedFile(project, element);
    }

    public Editor openInEditor(PsiElement selectedElement) {
        PsiFile psiFile;
        int i;
        if (selectedElement instanceof PsiFile) {
            psiFile = (PsiFile) selectedElement;
            i = -1;
        } else {
            psiFile = PluginPsiUtil.getContainingFile(selectedElement);
            i = selectedElement.getTextOffset();
        }

        if (psiFile == null) return null;

        final VirtualFile virtualFile = psiFile.getVirtualFile();

        if (virtualFile == null) return null;

        OpenFileDescriptor fileDesc = new OpenFileDescriptor(project, virtualFile, i);
        return FileEditorManager.getInstance(project).openTextEditor(fileDesc, false);
    }
}
