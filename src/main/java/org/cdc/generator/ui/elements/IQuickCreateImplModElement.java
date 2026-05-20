package org.cdc.generator.ui.elements;


import net.mcreator.util.StringUtils;
import org.cdc.generator.utils.Utils;

import javax.swing.*;

public interface IQuickCreateImplModElement {
    default JComponent registerCreateImplShortCut(JComponent panel) {
        var popupMenu = new JPopupMenu();
        for (String allSupportedGenerator : Utils.getAllSupportedGenerators()) {
            var menu = new JMenuItem(allSupportedGenerator);
            menu.addActionListener(a -> {
                createImpl(allSupportedGenerator, StringUtils.uppercaseFirstLetter(allSupportedGenerator).replaceAll("[.-]",""));
            });
            popupMenu.add(menu);
        }
        panel.setComponentPopupMenu(popupMenu);
        return panel;
    }

    void createImpl(String generator,String availableElementNameGenerator);
}
