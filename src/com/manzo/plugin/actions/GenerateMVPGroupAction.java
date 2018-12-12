package com.manzo.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.manzo.plugin.dialog.MVPInputDialog;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Created by shenjianlin on 2018/9/10.
 */
public class GenerateMVPGroupAction extends BaseFileAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
//        Project fatherProject = e.getProject();
//        if (fatherProject == null) {
//            return;
//        }
//        //获取所选的目录，即需要添加类的的包路径file
//        VirtualFile virtualFile = getSelectVirtualFile(e);
//
//        if (virtualFile == null || !virtualFile.isDirectory()) {
//            return;
//        }
//        //通过所选文件，获取包的directory
//        PsiDirectory directory = PsiDirectoryFactory.getInstance(fatherProject).createDirectory(virtualFile);
//        //添加类
//        JavaDirectoryService.getInstance().createClass(directory, "MyTest", "MVPTemplateClass");
        MVPInputDialog editDialog = new MVPInputDialog(e);
        editDialog.setSize(600, 360);
        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        editDialog.setVisible(true);
    }

}
