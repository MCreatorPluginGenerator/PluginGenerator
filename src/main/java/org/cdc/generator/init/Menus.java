package org.cdc.generator.init;

import net.mcreator.Launcher;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.plugin.events.ui.TabEvent;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;
import org.cdc.framework.utils.L10NHelper;
import org.cdc.generator.PluginMain;
import org.cdc.generator.services.types.ArgTypeProxy;
import org.cdc.generator.ui.elements.AbstractProceduresModElementGUI;
import org.cdc.generator.ui.elements.DataListModElementGUI;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.MenuProvider;
import org.cdc.generator.utils.builders.JMenuBuilder;
import org.cdc.generator.utils.builders.JMenuItemBuilder;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
            PLUGIN_PROCEDURE_UTILS.setVisible(event.getTab().getContent() instanceof AbstractProceduresModElementGUI<?>);
        });
    }

    public static void registerAllMenus(MCreator mcreator) {
        for (Supplier<JMenu> menu : menus) {
            mcreator.getMainMenuBar().add(menu.get());
        }
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
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("visit_repository")
                .setActionListener(a -> {
                    DesktopUtils.browseSafe("https://mcreator.net/repository");
                    // TODO: change to use updateinfo to select supported mcreator version.
                    // MCreatorApplication.WEB_API.getUpdateInfo();
                }).build());
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("visit_changelog")
                .setActionListener(a -> DesktopUtils.browseSafe("https://mcreator.net/changelog")).build());
        PLUGIN_MAKER.add(new JMenuItemBuilder().setParentMenuName("plugin_maker").setName("append_current")
                .setActionListener(a -> {
                    var selfDependants = "mcreator" + Launcher.version.versionlong;
                    if (!mcreator.getWorkspaceSettings().dependants.contains(selfDependants)) {
                        mcreator.getWorkspaceSettings().dependants.add(selfDependants);
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
        PLUGIN_PROCEDURE_UTILS.add(
                new JMenuItemBuilder().setParentMenuName("plugin_procedure_utils").setName("generate_warnings")
                        .setActionListener(a -> {
                            if (mcreator.getTabs().getCurrentTab()
                                    .getContent() instanceof AbstractProceduresModElementGUI<?> pluginProceduresElementGUI) {
                                for (String s : pluginProceduresElementGUI.getWarnings().getTextList()) {
                                    for (LinkedHashMap<String, String> value : mcreator.getWorkspace().getLanguageMap()
                                            .values()) {
                                        value.put(L10NHelper.getWarningKey(s), s);
                                    }
                                }
                            }
                        }).build());
        PLUGIN_PROCEDURE_UTILS.add(
                new JMenuItemBuilder().setParentMenuName("plugin_procedure_utils").setName("refresh_inputs_and_fields")
                        .setActionListener(a -> {
                            if (mcreator.getTabs().getCurrentTab()
                                    .getContent() instanceof AbstractProceduresModElementGUI<?> pluginProceduresElementGUI) {
                                System.out.println(mcreator.getTabs().getCurrentTab().getContent().getClass().getName());
                                var inputs = new ArrayList<String>();
                                var fields = new ArrayList<String>();
                                var statements = new ArrayList<String>();
                                for (ArgTypeProxy argTypeProxy : pluginProceduresElementGUI.getModel()) {
                                    if (argTypeProxy.getArg0Type().getType().equals(Arg0InputType.INPUT)) {
                                        inputs.add(argTypeProxy.getUniqueName());
                                    }
                                    if (argTypeProxy.getArg0Type().getType().equals(Arg0InputType.FIELD)) {
                                        fields.add(argTypeProxy.getUniqueName());
                                    }
                                    if (argTypeProxy.getArg0Type().getType().equals(Arg0InputType.STATEMENT)) {
                                        statements.add(argTypeProxy.getUniqueName());
                                    }
                                }
                                var inputs1 = new HashSet<String>();
                                inputs1.addAll(pluginProceduresElementGUI.getInputs().getTextList());
                                inputs1.addAll(inputs);
                                pluginProceduresElementGUI.getInputs().setTextList(inputs1);

                                var fields1 = new HashSet<String>();
                                fields1.addAll(pluginProceduresElementGUI.getFields().getTextList());
                                fields1.addAll(fields);
                                pluginProceduresElementGUI.getFields().setTextList(fields1);

                                var statements1 = new HashSet<String>();
                                statements1.addAll(pluginProceduresElementGUI.getStatements().getTextList());
                                statements1.addAll(statements);
                                pluginProceduresElementGUI.getStatements().setTextList(statements1);
                            }
                        }).build());
        PLUGIN_PROCEDURE_UTILS.add(new JMenuItemBuilder().setParentMenuName("plugin_procedure_utils").setName("load_block_color").setActionListener(a-> getExternalBlockColour(mcreator)).build());
        // TODO: Mapping_utils functions: like temporary plugin to add item and blocks.
    }

    private static void getExternalBlockColour(MCreator mcreator) {
        if (mcreator.getTabs().getCurrentTab()
                .getContent() instanceof AbstractProceduresModElementGUI<?> pluginProceduresElementGUI) {
            var blocks = BlocklyLoader.INSTANCE.getBlockLoader(pluginProceduresElementGUI.getBlocklyEditorType()).getDefinedBlocks();
            var blockName = JOptionPane.showInputDialog(mcreator,"Input block name");
            if (blocks.containsKey(blockName)){
                pluginProceduresElementGUI.setBuiltInColor(blocks.get(blockName).getBlocklyJSON().get("colour").getAsString());
            }
        }
    }
}
