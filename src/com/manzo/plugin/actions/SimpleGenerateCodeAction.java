package com.manzo.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.manzo.plugin.controller.SimpleFileController;
import com.manzo.plugin.dialog.MVPInputDialog;
import com.manzo.plugin.dialog.SimpleFormatSelectDialog;
import com.manzo.plugin.utils.AndroidUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by shenjianlin on 2018/8/30.
 * 导入Activity关联layout的控件
 */
public class SimpleGenerateCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
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
        SimpleFileController.loadFileByDialog(fatherProject, editor, file);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        AndroidUtils.layoutCodeCanLoad(e);
    }
}
