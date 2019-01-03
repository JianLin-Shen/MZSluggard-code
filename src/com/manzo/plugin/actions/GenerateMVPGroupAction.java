package com.manzo.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.manzo.plugin.dialog.MVPInputDialog;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by shenjianlin on 2018/9/10.
 * 生成MVP结构的Activity
 */
public class GenerateMVPGroupAction extends BaseFileAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        MVPInputDialog editDialog = new MVPInputDialog(e);
        editDialog.setSize(600, 360);
        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        editDialog.setVisible(true);
    }

}
