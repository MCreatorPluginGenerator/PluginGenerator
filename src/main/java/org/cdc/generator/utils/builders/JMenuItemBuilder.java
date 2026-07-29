package org.cdc.generator.utils.builders;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;
import org.jetbrains.annotations.Contract;

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

    @Contract("_->this") public JMenuItemBuilder setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
        return this;
    }

    @Contract("_->this") public JMenuItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Contract("_,_,_->this")
    public JMenuItemBuilder setInputListener(String title, String message, Consumer<String> inputListener) {
        this.actionListener = _ -> {
            var str = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
            if (str != null && !str.isEmpty()) {
                inputListener.accept(str);
            }
        };
        return this;
    }

    @Contract("_->this") public JMenuItemBuilder setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    @Contract("_->this") public JMenuItemBuilder setOpenURL(String url) {
        this.actionListener = _ -> {
            DesktopUtils.browseSafe(url);
        };
        return this;
    }

    @Contract("_,_,_->this")
    public <E extends JComponent> JMenuItemBuilder setCurrentModElementGUIConsumer(MCreator mCreator, Class<E> cls,
            Consumer<E> consumer) {
        this.actionListener = a -> {
            if (cls.isInstance(mCreator.getTabs().getCurrentTab().getContent())) {
                consumer.accept((E) mCreator.getTabs().getCurrentTab().getContent());
            }
        };
        return this;
    }

    public JMenuItem build() {
        JMenuItem menuitem = null;
        if (parentMenuName != null){
            menuitem = new JMenuItem(L10N.t("menus." + parentMenuName + "." + name));
        } else {
            menuitem = new JMenuItem(name);
        }
        menuitem.setName(name);
        menuitem.addActionListener(actionListener);
        return menuitem;
    }
}
