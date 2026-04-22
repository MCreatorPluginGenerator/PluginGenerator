package org.cdc.generator.utils;

import javax.swing.*;
import java.util.function.Supplier;

public class MenuProvider implements Supplier<JMenu> {

    public MenuProvider(Supplier<JMenu> menuSupplier) {
        this.menuSupplier = menuSupplier;
    }

    private final Supplier<JMenu> menuSupplier;
    private JMenu menu;
    private long lifeCycleTime;
    private boolean visible = true;

    @Override public JMenu get() {
        var million = System.currentTimeMillis() - lifeCycleTime;
        // refresh the menu
        if (menu == null || million > 10000L) {
            lifeCycleTime = System.currentTimeMillis();
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
}
