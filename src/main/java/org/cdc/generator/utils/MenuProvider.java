package org.cdc.generator.utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

public class MenuProvider implements Supplier<JMenu> {

    public MenuProvider(Supplier<JMenu> menuSupplier) {
        this.menuSupplier = menuSupplier;
        this.componentArrayList = new ArrayList<>();
    }

    private final Supplier<JMenu> menuSupplier;
    private JMenu menu;
    private boolean visible = true;
    private final ArrayList<Component> componentArrayList;

    @Override public JMenu get() {
        // refresh the menu
        menu = menuSupplier.get();
        menu.setVisible(visible);
        for (Component component : componentArrayList) {
            menu.add(component);
        }
        return menu;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (menu != null)
            this.menu.setVisible(this.visible);
    }

    public void add(Component component) {
        for (int i = 0; i < Collections.unmodifiableList(componentArrayList).size(); i++) {
            var component1 = componentArrayList.get(i);
            if (component1.getName().equals(component.getName())) {
                componentArrayList.set(i, component);
                return;
            }
        }
        componentArrayList.add(component);
    }
}
