package org.cdc.generator.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public class MenuProvider implements Supplier<JMenu> {

    public MenuProvider(Supplier<JMenu> menuSupplier) {
        this.menuSupplier = menuSupplier;
    }

    private final Supplier<JMenu> menuSupplier;
    private JMenu menu;
    private boolean visible = true;

    @Override public JMenu get() {
        // refresh the menu
        if (menu == null) {
            menu = menuSupplier.get();
            menu.setVisible(visible);
        }
        return menu;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (menu != null)
            this.menu.setVisible(this.visible);
    }

    public void add(Component component) {
        if (menu != null) {
            if (Arrays.stream(menu.getMenuComponents())
                    .noneMatch(a -> Objects.equals(component.getName(), a.getName()))) {
                menu.add(component);
            }
        }
    }
}
