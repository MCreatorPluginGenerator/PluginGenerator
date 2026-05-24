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

/*    @Override public TableCellRenderer getDefaultRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            var label = L10N.label("elementgui." + helpPrefix + ".table." + value.toString().replace(' ', '_')
                    .toLowerCase(Locale.ROOT));
            label.setBorder(BorderFactory.createLineBorder(Theme.current().getForegroundColor()));
            return label;
        };
    }*/

    @Override public String getToolTipText(MouseEvent event) {
        java.awt.Point p = event.getPoint();
        int viewColumnIndex = columnModel.getColumnIndexAtX(p.x);
        var column = columnModel.getColumn(viewColumnIndex);
        String object = column.getHeaderValue().toString();

        return HelpLoader.loadHelpFor(
                gui.withEntry(helpPrefix + "/table/" + object.replace(' ', '_').toLowerCase(Locale.ROOT)), true);
    }
}
