package org.cdc.generator.init;

import net.mcreator.Launcher;
import net.mcreator.plugin.events.ui.TabEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import org.cdc.generator.PluginMain;
import org.cdc.generator.ui.elements.AbstractProceduresModElementGUI;
import org.cdc.generator.ui.elements.DataListModElementGUI;
import org.cdc.generator.utils.MenuProvider;
import org.cdc.generator.utils.builders.JMenuBuilder;
import org.cdc.generator.utils.builders.JMenuItemBuilder;
import org.cdc.generator.utils.interfaces.IMenusProvider;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

public class Menus {
    public static MenuProvider PLUGIN_MAKER = register(() -> L10N.menu("menus.plugin_maker"));
    public static MenuProvider DATALIST_UTILS = register(() -> L10N.menu("menus.datalist_utils"));
    public static MenuProvider PLUGIN_PROCEDURE_UTILS = register(() -> L10N.menu("menus.plugin_procedure_utils"));

    private static ArrayList<Supplier<JMenu>> menus;

    private static MenuProvider register(final Supplier<JMenu> menuSupplier) {
        if (menus == null) {
            menus = new ArrayList<>();
        }
        var supplier = new MenuProvider(menuSupplier);
        menus.add(supplier);
        return supplier;
    }

    public static void registerMenuVisibleControls(PluginMain pluginMain) {
        DATALIST_UTILS.setVisible(false);
        PLUGIN_PROCEDURE_UTILS.setVisible(false);
        pluginMain.addListener(TabEvent.Shown.class, event -> {
            DATALIST_UTILS.setVisible(event.getTab().getContent() instanceof DataListModElementGUI);
            PLUGIN_PROCEDURE_UTILS.setVisible(
                    event.getTab().getContent() instanceof AbstractProceduresModElementGUI<?>);
        });
    }

    public static void registerAllMenus(MCreator mcreator) {
        for (Supplier<JMenu> menu : menus) {
            mcreator.getMainMenuBar().add(menu.get());
        }

        // extension point for other plugins
        IMenusProvider.serviceLoader.stream().forEach(a -> {
            var menu = a.get().provide(mcreator);
            if (menu != null) {
                mcreator.getMainMenuBar().add(menu);
            }
        });
    }

    public static void registerAllSubMenus(MCreator mcreator) {
        PLUGIN_MAKER.add(
                new JMenuBuilder().setParentMenuName("plugin_maker").setName("load_from_external").setReload(a -> {
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
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("open_plugin_page")
                .setOpenURL("https://mcreator.net/plugin/122734/pluginmaker").build());
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("visit_repository")
                .setOpenURL("https://mcreator.net/repository").build());
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("visit_changelog")
                .setOpenURL("https://mcreator.net/changelog").build());
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("append_current")
                .setActionListener(a -> {
                    var selfDependants = "mcreator" + Launcher.version.versionlong;
                    if (!mcreator.getWorkspaceSettings().dependants.contains(selfDependants)) {
                        mcreator.getToolkit().beep();
                        mcreator.getWorkspaceSettings().dependants.add(selfDependants);
                        mcreator.getStatusBar().setPersistentMessage("Appended");
                    }
                }).build());
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("append_current_major")
                .setActionListener(a -> {
                    var selfDependants = "mcreator" + Launcher.version.majorlong;
                    if (!mcreator.getWorkspaceSettings().dependants.contains(selfDependants)) {
                        mcreator.getWorkspaceSettings().dependants.add(selfDependants);
                        mcreator.getToolkit().beep();
                        mcreator.getStatusBar().setPersistentMessage("Appended");
                    }
                }).build());
        DATALIST_UTILS.add(
                new JMenuBuilder().setParentMenuName("datalist_utils").setName("calculate_types").setReload(jMenu -> {
                    if (mcreator.getTabs().getCurrentTab()
                            .getContent() instanceof DataListModElementGUI dataListModElementGUI) {
                        jMenu.removeAll();
                        dataListModElementGUI.getTypes().forEach(b -> {
                            if (b == null || b.isBlank()) {
                                return;
                            }
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
