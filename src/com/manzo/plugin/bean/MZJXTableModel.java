package com.manzo.plugin.bean;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MZJXTableModel extends AbstractTableModel {
    public static final int CHOICE_BOX_INDEX = 0;
    public static final int CLICK_BOX_INDEX = 3;
    private static final int ID_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private List<AndroidView> mAndroidViews;
    // 定义表头数据
    private String[] mHead = {"添加对象", "id", "name", "添加Click"};
    // 创建类型数组
    // Class[]
    // mTypeArray={Object.class,Object.class,Boolean.class,int.class,Object.class,Object.class};

    public MZJXTableModel(List<AndroidView> androidViews) {
        mAndroidViews = androidViews;
    }

    // 定义表格每一列的数据类型

    private Class[] mTypeArray = {Boolean.class, String.class, String.class,
            Boolean.class};


    // 获得表格的列数
    public int getColumnCount() {
        return mHead.length;
    }

    // 获得表格的行数
    public int getRowCount() {

        return mAndroidViews == null ? 0 : mAndroidViews.size();
    }

    // 获得表格的列名称
    @Override
    public String getColumnName(int column) {
        return mHead[column];
    }

    // 获得表格的单元格的数据
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= mAndroidViews.size()) {
            return null;
        }
        AndroidView androidView = mAndroidViews.get(rowIndex);
        Object tData = null;
        switch (columnIndex) {
            case CHOICE_BOX_INDEX:
                tData = androidView.isChoice;
                break;
            case ID_INDEX:
                tData = androidView.getId();
                break;
            case NAME_INDEX:
                tData = androidView.getFieldName();
                break;
            case CLICK_BOX_INDEX:
                tData = androidView.isClick;
                break;
            default:
                break;
        }
        return tData;
    }

    // 使表格具有可编辑性
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //只有第二个id不可编辑
        return columnIndex != ID_INDEX;
    }

    public void selectAllOrNull(boolean value, int columnIndex) {
        if (columnIndex == CHOICE_BOX_INDEX || columnIndex == CLICK_BOX_INDEX) {
            for (int i = 0; i < getRowCount(); i++) {
                this.setValueAt(value, i, columnIndex);
            }
        }

    }

    // 替换单元格的值
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= mAndroidViews.size()) {
            return;
        }
        AndroidView androidView = mAndroidViews.get(rowIndex);
        switch (columnIndex) {
            case CHOICE_BOX_INDEX:
                if (aValue instanceof Boolean) {
                    androidView.isChoice = (Boolean) aValue;
                }
                break;
            case NAME_INDEX:
                if (aValue instanceof String) {
                    androidView.setFieldName((String) aValue);
                }
                break;
            case CLICK_BOX_INDEX:
                if (aValue instanceof Boolean) {
                    androidView.isClick = (Boolean) aValue;
                }
                break;
            default:
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    // 实现了如果是boolean自动转成JCheckbox
    /*
     * 需要自己的celleditor这么麻烦吧。jtable自动支持Jcheckbox，
	 * 只要覆盖tablemodel的getColumnClass返回一个boolean的class， jtable会自动画一个Jcheckbox给你，
	 * 你的value是true还是false直接读table里那个cell的值就可以
	 */
    public Class getColumnClass(int columnIndex) {
        return mTypeArray[columnIndex];// 返回每一列的数据类型
    }
}