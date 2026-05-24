package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginCmdArgsCategoryModElement;
import org.cdc.generator.elements.PluginCmdArgsProcedureModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.cdc.generator.init.Menus;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.ioc.InjectField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/5/25
 */
public class PluginCmdArgsProcedureModElementGUI extends AbstractProceduresModElementGUI<PluginCmdArgsProcedureModElement> implements IQuickCreateImplModElement{
    @InjectField Container container;

    public PluginCmdArgsProcedureModElementGUI(MCreator mcreator, @NotNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode);

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        super.initGUI();
        var config = buildConfiguration(2);
        config.getComponentPopupMenu().add(Menus.PLUGIN_PROCEDURE_UTILS.get());
        Utils.registerCreateImplShortCut(this, this);

        var component = PanelUtils.northAndCenterElement(config, toolbarAndTable(dependenciesToolBar));
        Utils.registerCreateImplShortCut(this, component);

        addPage("Configuration", component).validate(name).validate(localizationValue);
        addPage("Args0", PanelUtils.northAndCenterElement(args0ToolBar, splitPane));
    }

    @Override protected void openInEditingMode(PluginCmdArgsProcedureModElement generatableElement) {
        super.openInEditingMode0(generatableElement);
    }

    @Override public PluginCmdArgsProcedureModElement getElementFromGUI() {
        this.modElement.setRegistryName(name.getText());
        var element = new PluginCmdArgsProcedureModElement(modElement);
        element.inputsInline = this.inputsInline.isSelected();
        element.previousStatement = this.previousStatement.getText();
        element.nextStatement = this.nextStatement.getText();
        element.colour = this.color.getColor();
        element.builtInColor = Utils.nullToNoneOrNoneToNull(builtInColor.getSelectedItem(),false);
        element.mutator = this.mutator.getText();
        // compatible with previous version.
        element.outputs = outputs.getListElements();
        element.extensions = this.extensions.getTextList();
        element.toolbox_id = this.toolboxId.getSelectedItem();
        element.group = this.group.getText();
        element.warnings = Objects.requireNonNullElse(this.warnings.getTextList(), List.of());
        element.required_apis = Objects.requireNonNullElse(requiredApis.getListElements(), List.of());
        element.arg0 = new ArrayList<>(model);
        element.inputs = this.inputs.getTextList();
        element.fields = this.fields.getTextList();
        element.statements = this.statements.getTextList();
        element.toolbox_init = this.toolboxInit.getTextList();
        element.dependencies = dependencies.stream().map(a -> {
            try {
                return (PluginProcedureModElement.Dependency) a.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        element.localization = localizationValue.getText();
        element.tooltip = tooltip.getText();
        return element;
    }

    @Override protected Container getContainer() {
        return container;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public BlocklyEditorType getBlocklyEditorType() {
        return BlocklyEditorType.COMMAND_ARG;
    }

    @Override public Class<? extends IBlocklyCategoryElement> getBlocklyCategoryClass() {
        return PluginCmdArgsCategoryModElement.class;
    }

    @Override public boolean hasBuiltinCategories() {
        return false;
    }

    @Override public void createImpl(String generator, String availableElementNameGenerator) {
        ModElement modElement1 = new ModElement(mcreator.getWorkspace(),
                modElement.getName() + "PluginCmdArgsProcedureImpl" + availableElementNameGenerator, ModElementTypes.PROCEDURE_IMPLEMENTATION);
        PluginProcedureImplementationModElementGUI element = (PluginProcedureImplementationModElementGUI) ModElementTypes.PROCEDURE_IMPLEMENTATION.getModElementGUI(
                mcreator, modElement1, false);
        element.procedureFileName.setSelectedItem(this.name.getText());
        element.generator.setSelectedItem(generator);
        element.parentFolder.setText("cmdargs");
        element.showView();
    }

    @Override protected String getHelpEntryAndLocalizationPrefix() {
        return "pluginprocedure";
    }
}
