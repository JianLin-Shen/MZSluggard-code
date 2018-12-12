package com.manzo.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by shenjianlin on 2018/12/12.
 */
public abstract class BaseFileAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        Project project = e.getProject();
        if (project == null) {
            presentation.setEnabled(false);
            return;
        }
        presentation.setEnabled(selectorNotJavaPackage(e));
    }

    private boolean selectorNotJavaPackage(AnActionEvent e) {
        VirtualFile virtualFile = getSelectVirtualFile(e);
        if (virtualFile == null || !virtualFile.isDirectory()) {
            return false;
        }
        String pathStr = virtualFile.getPath();
        if (StringUtils.isBlank(pathStr)) {
            return false;
        }
        return pathStr.contains("src/main/java");
    }

    private @Nullable
    VirtualFile getSelectVirtualFile(AnActionEvent e) {
        return CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    }
}
