package org.cdc.generator.ui.elements;


import net.mcreator.ui.init.L10N;
import net.mcreator.util.StringUtils;
import org.cdc.generator.utils.Utils;

import javax.swing.*;

public interface IQuickCreateImplModElement {
    default JComponent registerCreateImplShortCut(JComponent panel) {
        var menus = L10N.menu("menus.simple_create_impl");
        for (String allSupportedGenerator : Utils.getAllSupportedGenerators()) {
            var menu = new JMenuItem(allSupportedGenerator);
            menu.addActionListener(a -> {
                createImpl(allSupportedGenerator,
                        StringUtils.uppercaseFirstLetter(allSupportedGenerator).replaceAll("[.-]", ""));
            });
            menus.add(menu);
        }
        if (panel.getComponentPopupMenu() == null) {
            var popupMenu = new JPopupMenu();
            popupMenu.add(menus);
            panel.setComponentPopupMenu(popupMenu);
        } else {
            panel.getComponentPopupMenu().add(menus);
        }
        return panel;
    }

    void createImpl(String generator,String availableElementNameGenerator);
}
