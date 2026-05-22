package org.cdc.generator.ui;

import net.mcreator.ui.help.HelpLoader;
import net.mcreator.ui.modgui.ModElementGUI;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseEvent;
import java.util.Locale;

public class HelpTableHeader extends JTableHeader {

    private ModElementGUI<?> gui;
    private String helpPrefix;

    public HelpTableHeader(TableColumnModel cm, ModElementGUI<?> gui, String helpPrefix) {
        super(cm);

        this.gui = gui;
        this.helpPrefix = helpPrefix;
    }

//    @Override public TableCellRenderer getDefaultRenderer() {
//        return (table, value, isSelected, hasFocus, row, column) -> L10N.label(
//                "elementgui." + helpPrefix + ".table." + value.toString().replace(' ', '_').toLowerCase(Locale.ROOT));
//    }

    @Override public String getToolTipText(MouseEvent event) {
        java.awt.Point p = event.getPoint();
        // 根据坐标获取当前所在的列索引（视图索引）
        int viewColumnIndex = columnModel.getColumnIndexAtX(p.x);
        var column = columnModel.getColumn(viewColumnIndex);
        // 将视图索引转换为模型索引，确保排序等操作后提示依然正确
        String object = column.getHeaderValue().toString();

        return HelpLoader.loadHelpFor(
                gui.withEntry(helpPrefix + "/table/" + object.replace(' ', '_').toLowerCase(Locale.ROOT)), false);
    }
}
