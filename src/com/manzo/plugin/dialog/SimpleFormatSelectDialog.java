package com.manzo.plugin.dialog;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.manzo.plugin.bean.AndroidView;
import com.manzo.plugin.bean.CheckHeaderCellRenderer;
import com.manzo.plugin.bean.MZJXTableModel;
import com.manzo.plugin.controller.SimpleFileController;
import com.manzo.plugin.utils.AndroidUtils;
import org.jdesktop.swingx.JXTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.*;
import java.util.List;

public class SimpleFormatSelectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JScrollPane mScrollTable;
    private List<AndroidView> mAndroidViews;

    private final Project mProject;
    private final Editor mEditor;
    private final PsiFile mCurrentFile;

    public static void showDialog(@NotNull Project project, Editor editor, PsiFile currentFile) {
        String layoutName = editor.getSelectionModel().getSelectedText();
        PsiFile xmlFile = AndroidUtils.findXmlResource(project, layoutName);
        if (xmlFile == null) {
            return;
        }
        //获取到layout中的view对象
        List<AndroidView> androidViews = AndroidUtils.getIDsFromXML(xmlFile);

        SimpleFormatSelectDialog editDialog = new SimpleFormatSelectDialog(project, editor, currentFile, androidViews);
        editDialog.setSize(600, 360);
        editDialog.setLocationRelativeTo(null);
        editDialog.setResizable(false);
        editDialog.setVisible(true);
    }

    public SimpleFormatSelectDialog(@NotNull Project project, Editor editor, PsiFile currentFile, List<AndroidView> androidViews) {
        this.mProject = project;
        this.mEditor = editor;
        this.mCurrentFile = currentFile;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.mAndroidViews = androidViews;
        initViewTable(mAndroidViews);
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

    private void initViewTable(List<AndroidView> androidViews) {
        MZJXTableModel myModel = new MZJXTableModel(androidViews);

        // JTable
        JXTable table = new JXTable(myModel);
        // 获得表格的表格列类
        TableColumn columnChoice = table.getColumnModel().getColumn(MZJXTableModel.CHOICE_BOX_INDEX);
        TableColumn columnClick = table.getColumnModel().getColumn(MZJXTableModel.CLICK_BOX_INDEX);

        // 实例化JCheckBox
        columnChoice.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        columnChoice.setPreferredWidth(60);
        columnClick.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        columnClick.setPreferredWidth(60);
        table.getTableHeader().setDefaultRenderer(new CheckHeaderCellRenderer(table));
        // 获得自定义的抽象表格模型
        mScrollTable.setViewportView(table);
    }

    private void onOK() {
        // add your code here
        SimpleFileController.loadFile(mProject, mEditor, mCurrentFile, mAndroidViews);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
