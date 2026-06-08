package org.cdc.generator.services.menus;

import net.mcreator.ui.MCreator;
import org.cdc.framework.utils.L10NHelper;
import org.cdc.generator.services.types.ArgTypeProxy;
import org.cdc.generator.ui.elements.AbstractProceduresModElementGUI;
import org.cdc.generator.ui.elements.PluginProceduresModElementGUI;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ElementsUtils;
import org.cdc.generator.utils.builders.JMenuItemBuilder;
import org.cdc.generator.utils.interfaces.IMenusProvider;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static org.cdc.generator.init.Menus.PLUGIN_PROCEDURE_UTILS;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/6/8
 */
public class PluginProcedureUtilsMenuProvider implements IMenusProvider {
    @Override public JMenu provide(MCreator mcreator) {
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
                        .setCurrentModElementGUIConsumer(mcreator, PluginProceduresModElementGUI.class,
                                pluginProceduresElementGUI -> {
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

                                }).build());
        PLUGIN_PROCEDURE_UTILS.add(
                new JMenuItemBuilder().setParentMenuName("plugin_procedure_utils").setName("load_block_color")
                        .setCurrentModElementGUIConsumer(mcreator, AbstractProceduresModElementGUI.class,
                                pluginProceduresElementGUI -> {
                                    var blockName = JOptionPane.showInputDialog(mcreator, "Input block name");
                                    ElementsUtils.getExternalBlockColour(blockName,
                                            pluginProceduresElementGUI.getBlocklyEditorType());
                                }).build());
        return null;
    }
}
