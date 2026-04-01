package org.cdc.generator.init;

import net.mcreator.plugin.events.ui.TabEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;
import org.cdc.generator.PluginMain;
import org.cdc.generator.elements.DataListModElement;
import org.cdc.generator.ui.elements.DataListModElementGUI;
import org.cdc.generator.utils.Constants;
import org.cdc.generator.utils.builders.JMenuBuilder;
import org.cdc.generator.utils.builders.JMenuItemBuilder;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Menus {
    public static Supplier<JMenu> PLUGIN_MAKER = register(() -> L10N.menu("menus.plugin_maker"));
    public static Supplier<JMenu> DATALIST_UTILS = register(() -> L10N.menu("menus.datalist_utils"));

    private static ArrayList<Supplier<JMenu>> menus;

    private static Supplier<JMenu> register(final Supplier<JMenu> menuSupplier) {
        if (menus == null) {
            menus = new ArrayList<>();
        }
        var supplier = new Supplier<JMenu>() {
            private JMenu menu;

            @Override public JMenu get() {
                if (menu == null) {
                    menu = menuSupplier.get();
                }
                return menu;
            }
        };
        menus.add(supplier);
        return supplier;
    }

    public static void registerMenuVisibleControls(PluginMain pluginMain) {
        pluginMain.addListener(TabEvent.Shown.class, event -> {
            DATALIST_UTILS.get().setVisible(event.getTab().getContent() instanceof DataListModElementGUI);
        });
    }

    public static void registerAllMenus(MCreator mcreator) {
        for (Supplier<JMenu> menu : menus) {
            mcreator.getMainMenuBar().add(menu.get());
        }
    }

    public static void registerAllSubMenus(MCreator mcreator) {
        PLUGIN_MAKER.get()
                .add(new JMenuBuilder().setParentMenuName("plugin_maker").setName("load_from_external").setReload(a -> {
                    final var langmap = mcreator.getWorkspace().getLanguageMap();
                    for (String s : langmap.keySet()) {
                        var menuItem = new JMenuItem(s);
                        menuItem.addActionListener(b -> {
                            var file = FileDialogs.getOpenDialog(mcreator, new String[] { "*.properties" });
                            Properties properties = new Properties();
                            try {
                                properties.load(new FileReader(file));
                                var en = langmap.get(s);
                                for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                                    en.put(objectObjectEntry.getKey().toString(),
                                            objectObjectEntry.getValue().toString());
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        a.add(menuItem);
                    }
                }).build());
        PLUGIN_MAKER.get().add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("visit_repository")
                .setActionListener(a -> {
                    DesktopUtils.browseSafe("https://mcreator.net/repository");
                    // TODO: change to use updateinfo to select supported mcreator version.
                    // MCreatorApplication.WEB_API.getUpdateInfo();
                }).build());
        PLUGIN_MAKER.get().add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("visit_changelog")
                .setActionListener(a -> {
                    DesktopUtils.browseSafe("https://mcreator.net/changelog");
                }).build());
        DATALIST_UTILS.get().add(new JMenuBuilder().setParentMenuName("datalist_utils").setName("builtin_entries")
                .setInit(menu -> Stream.of(Constants.builtEntriesInDataList).forEach(a -> {
                    JMenuItem menuItem = new JMenuItem(a);
                    menuItem.addActionListener(event -> {
                        if (mcreator.getTabs().getCurrentTab()
                                .getContent() instanceof DataListModElementGUI dataListModElementGUI) {
                            dataListModElementGUI.entries.add(new DataListModElement.DataListEntry(a));
                            dataListModElementGUI.refreshTable();
                            JOptionPane.showMessageDialog(mcreator, "Added");
                        }
                    });
                    menu.add(menuItem);
                })).build());
        DATALIST_UTILS.get().add(new JMenuBuilder().setParentMenuName("datalist_utils").setName("calculate_types")
                .setReload(jMenu -> {
                    if (mcreator.getTabs().getCurrentTab()
                            .getContent() instanceof DataListModElementGUI dataListModElementGUI) {
                        jMenu.removeAll();
                        dataListModElementGUI.getTypes().forEach(b -> {
                            var menuItem = new JMenuItem(b);
                            menuItem.addActionListener(e1 -> {
                                var content = new StringSelection(b);
                                mcreator.getToolkit().getSystemClipboard().setContents(content, content);
                                JOptionPane.showMessageDialog(mcreator, "Copied");
                            });
                            jMenu.add(menuItem);
                        });
                    }
                }).build());
        // TODO: Mapping_utils functions: like temporary plugin to add item and blocks.
    }
}
