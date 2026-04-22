package org.cdc.generator.utils;

import javax.swing.*;
import java.util.function.Supplier;

public class MenuProvider implements Supplier<JMenu> {

    public MenuProvider(Supplier<JMenu> menuSupplier){
        this.menuSupplier = menuSupplier;
    }

    private Supplier<JMenu> menuSupplier;
    private JMenu menu;
    private long lifeCycleTime;

    @Override public JMenu get() {
        var million = System.currentTimeMillis() - lifeCycleTime;
        // refresh the menu
        if (menu == null || million > 10000L) {
            lifeCycleTime = System.currentTimeMillis();
            menu = menuSupplier.get();
        }
        return menu;
    }

    public void setVisible(boolean visible) {
        this.menu.setVisible(visible);
    }
}
