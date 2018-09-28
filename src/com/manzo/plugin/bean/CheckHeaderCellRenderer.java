package com.manzo.plugin.bean;

import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckHeaderCellRenderer implements TableCellRenderer {
    private MZJXTableModel mTableModel;
    private JTableHeader mTableHeader;
    private final JCheckBox mChoiceSelectBox;
    private final JCheckBox mClickBox;

    public CheckHeaderCellRenderer(JXTable table) {
        this.mTableModel = (MZJXTableModel) table.getModel();
        this.mTableHeader = table.getTableHeader();
        mChoiceSelectBox = new JCheckBox(mTableModel.getColumnName(MZJXTableModel.CHOICE_BOX_INDEX));
        mClickBox = new JCheckBox(mTableModel.getColumnName(MZJXTableModel.CLICK_BOX_INDEX));
        mClickBox.setSelected(true);
        mChoiceSelectBox.setSelected(true);
        mTableHeader.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    //获得选中列
                    int selectColumn = mTableHeader.columnAtPoint(e.getPoint());
                    if (selectColumn == MZJXTableModel.CHOICE_BOX_INDEX) {
                        boolean value = !mChoiceSelectBox.isSelected();
                        mChoiceSelectBox.setSelected(value);
                        mTableModel.selectAllOrNull(value, selectColumn);
                        mTableHeader.repaint();
                    } else if (selectColumn == MZJXTableModel.CLICK_BOX_INDEX) {
                        boolean value = !mClickBox.isSelected();
                        mClickBox.setSelected(value);
                        mTableModel.selectAllOrNull(value, selectColumn);
                        mTableHeader.repaint();
                    }
                }
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        // TODO Auto-generated method stub
        String valueStr = (String) value;
        JLabel label = new JLabel(valueStr);
        label.setHorizontalAlignment(SwingConstants.CENTER); // 表头标签剧中
        mChoiceSelectBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签剧中
        mChoiceSelectBox.setBorderPainted(true);
        mClickBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签剧中
        mClickBox.setBorderPainted(true);
        JComponent component = label;
        if (column == MZJXTableModel.CHOICE_BOX_INDEX) {
            component = mChoiceSelectBox;
        }
        if (column == MZJXTableModel.CLICK_BOX_INDEX) {
            component = mClickBox;
        }
        component.setForeground(mTableHeader.getForeground());
        component.setBackground(mTableHeader.getBackground());
        component.setFont(mTableHeader.getFont());
        component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        return component;
    }

}