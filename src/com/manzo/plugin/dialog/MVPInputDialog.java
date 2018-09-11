package com.manzo.plugin.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.manzo.plugin.utils.JavaCommonUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class MVPInputDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField mActivityNameEditor;
    private JTextPane mTips;
    private final AnActionEvent mAnActionEvent;


    public MVPInputDialog(AnActionEvent e) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mAnActionEvent = e;
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        String activity = mActivityNameEditor.getText();
        if (StringUtils.isBlank(activity)) {
            mTips.setText("请填写activity名称");
            return;
        }
        //TODO 添加代码操作
        Project fatherProject = mAnActionEvent.getProject();
        if (fatherProject == null) {
            return;
        }
        //获取所选的目录，即需要添加类的的包路径file
        VirtualFile virtualFile = JavaCommonUtils.getSelectVirtualFile(mAnActionEvent);

        if (virtualFile == null || !virtualFile.isDirectory()) {
            return;
        }
        //通过所选文件，获取包的directory
        PsiDirectory directory = PsiDirectoryFactory.getInstance(fatherProject).createDirectory(virtualFile);
        //添加类
        String actName = activity + "Activity";
        JavaDirectoryService.getInstance().createClass(directory, actName, "MVPTemplateClass");
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
