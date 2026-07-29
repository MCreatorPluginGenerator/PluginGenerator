package org.cdc.generator.utils.builders;

import net.mcreator.ui.init.L10N;
import org.jetbrains.annotations.Contract;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.function.Consumer;

public class JMenuBuilder {
    private String parentMenuName;
    private String name;
    private Consumer<JMenu> reload;
    private Consumer<JMenu> init;

    public JMenuBuilder() {

    }

    @Contract("_->this") public JMenuBuilder setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
        return this;
    }

    @Contract("_->this") public JMenuBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Contract("_->this") public JMenuBuilder setReload(Consumer<JMenu> reload) {
        this.reload = reload;
        return this;
    }

    @Contract("_->this") public JMenuBuilder setInit(Consumer<JMenu> init) {
        this.init = init;
        return this;
    }

    public JMenu build() {
        JMenu menu = parentMenuName == null ? new JMenu(name) : L10N.menu("menus." + parentMenuName + "." + name);
        menu.setName(name);
        if (init != null) {
            init.accept(menu);
        }
        if (reload != null) {
            menu.addMenuListener(new MenuListener() {
                @Override public void menuSelected(MenuEvent e) {
                    menu.removeAll();
                    reload.accept(menu);
                }

                @Override public void menuDeselected(MenuEvent e) {

                }

                @Override public void menuCanceled(MenuEvent e) {

                }
            });
        }
        return menu;
    }
}
