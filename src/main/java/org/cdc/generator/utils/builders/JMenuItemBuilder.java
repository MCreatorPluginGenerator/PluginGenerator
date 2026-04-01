package org.cdc.generator.utils.builders;

import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * if you hope to localization your menuitem, please use it.
 */
public class JMenuItemBuilder {
    private String parentMenuName;
    private String name;
    private ActionListener actionListener;

    public JMenuItemBuilder() {

    }

    public JMenuItemBuilder setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
        return this;
    }

    public JMenuItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JMenuItemBuilder setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    public JMenuItem build() {
        var menuitem = new JMenuItem(L10N.t("menus." + parentMenuName + "." + name));
        menuitem.addActionListener(actionListener);
        return menuitem;
    }
}
