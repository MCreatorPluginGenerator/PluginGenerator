package org.cdc.generator.ui.elements;


import net.mcreator.util.StringUtils;
import org.cdc.generator.utils.Utils;

import javax.swing.*;

public interface IHasImplModElement {
    default void registerShortCut(JComponent panel) {
        JPopupMenu popupMenu = new JPopupMenu();
        for (String allSupportedGenerator : Utils.getAllSupportedGenerators()) {
            var menu = new JMenuItem(allSupportedGenerator);
            menu.addActionListener(a -> {
                createImpl(allSupportedGenerator, StringUtils.uppercaseFirstLetter(allSupportedGenerator).replaceAll("[.-]",""));
            });
            popupMenu.add(menu);
        }
        panel.setComponentPopupMenu(popupMenu);
    }

    void createImpl(String generator,String availableElementNameGenerator);
}
