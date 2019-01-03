package com.manzo.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.manzo.plugin.controller.SimpleFileController;
import com.manzo.plugin.utils.AndroidUtils;

/**
 * Created by shenjianlin on 2018/9/5.
 * 自动导入Adapter关联layout中的控件
 */
public class GenerateCodeInAdapterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        Project fatherProject = anActionEvent.getProject();
        if (fatherProject == null) {
            return;
        }
        Editor editor = CommonDataKeys.EDITOR.getData(anActionEvent.getDataContext());
        if (editor == null) {
            return;
        }
        PsiFile file = anActionEvent.getData(PlatformDataKeys.PSI_FILE);
        if (file == null) {
            return;
        }
        SimpleFileController.loadFileToAdapter(fatherProject, editor, file);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        AndroidUtils.layoutCodeCanLoad(e);
    }
}
