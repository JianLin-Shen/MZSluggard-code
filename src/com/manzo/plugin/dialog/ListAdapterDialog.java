package com.manzo.plugin.dialog;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.manzo.plugin.controller.ListCodeAdapterController;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class ListAdapterDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField mBeanEditText;
    private JTextField mAdapterEditText;
    private JTextArea tipsTextArea;
    private final AnActionEvent mAnActionEvent;


    public ListAdapterDialog(AnActionEvent e) {
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
        String adapterName = mAdapterEditText.getText();
        if (StringUtils.isBlank(adapterName)) {
            tipsTextArea.setText("请填写adapter名称");
            return;
        }
        String beanName = mBeanEditText.getText();
        if (StringUtils.isBlank(beanName)) {
            tipsTextArea.setText("请填写BeanName");
            return;
        }
        ListCodeAdapterController.generateAdapterCode(adapterName, beanName, mAnActionEvent);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
