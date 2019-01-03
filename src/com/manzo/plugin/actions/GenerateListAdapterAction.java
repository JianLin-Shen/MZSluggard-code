package com.manzo.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.manzo.plugin.dialog.ListAdapterDialog;

/**
 * Created by shenjianlin on 2018/12/13.
 */
public class GenerateListAdapterAction extends BaseFileAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        ListAdapterDialog editDialog = new ListAdapterDialog(e);
        editDialog.setSize(600, 360);
        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        editDialog.setVisible(true);
    }
}
