package org.cdc.generator.utils.builders;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

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

    public JMenuItemBuilder setOpenURL(String url){
        this.actionListener = a->{
            DesktopUtils.browseSafe(url);
        };
        return this;
    }

    public <E extends JComponent> JMenuItemBuilder setCurrentModElementGUIConsumer(MCreator mCreator,Class<E> cls, Consumer<E> consumer){
        this.actionListener = a->{
            if (cls.isInstance(mCreator.getTabs().getCurrentTab().getContent())){
                consumer.accept((E)mCreator.getTabs().getCurrentTab().getContent());
            }
        };
        return this;
    }

    public JMenuItem build() {
        var menuitem = new JMenuItem(L10N.t("menus." + parentMenuName + "." + name));
        menuitem.setName(name);
        menuitem.addActionListener(actionListener);
        return menuitem;
    }
}
