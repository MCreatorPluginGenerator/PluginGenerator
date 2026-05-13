package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.elements.ProcedureCategoryModElement;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.ioc.InjectField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginProceduresModElementGUI extends AbstractProceduresModElementGUI<PluginProcedureModElement> {
    @InjectField private Container container;

    public PluginProceduresModElementGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        super.initGUI();
        addPage("Configuration",
                PanelUtils.northAndCenterElement(buildConfiguration(2), toolbarAndTable(dependenciesToolBar))).validate(
                name).validate(localizationValue);
        addPage("Args0", PanelUtils.northAndCenterElement(args0ToolBar, splitPane));
    }

    @Override protected Container getContainer() {
        return container;
    }

    @Override protected void openInEditingMode(PluginProcedureModElement generatableElement) {
        super.openInEditingMode0(generatableElement);
    }

    @Override public PluginProcedureModElement getElementFromGUI() {
        this.modElement.setRegistryName(name.getText());
        var element = new PluginProcedureModElement(modElement);
        element.inputsInline = this.inputsInline.isSelected();
        element.previousStatement = this.previousStatement.getText();
        element.nextStatement = this.nextStatement.getText();
        element.colour = this.color.getColor();
        element.builtInColor = Utils.nullToNoneOrNoneToNull(builtInColor.getSelectedItem());
        element.mutator = this.mutator.getText();
        // compatible with previous version.
        element.outputs = outputs.getListElements();
        element.extensions = this.extensions.getTextList();
        element.toolbox_id = this.toolboxId.getSelectedItem();
        element.group = this.group.getText();
        element.warnings = Objects.requireNonNullElse(this.warnings.getTextList(), List.of());
        element.required_apis = Objects.requireNonNullElse(requiredApis.getTextList(), List.of());
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

    @Override @Nullable public URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public BlocklyEditorType getBlocklyEditorType() {
        return BlocklyEditorType.PROCEDURE;
    }

    @Override public Class<? extends IBlocklyCategoryElement> getBlocklyCategoryClass() {
        return ProcedureCategoryModElement.class;
    }

    @Override public boolean hasBuiltinCategories() {
        return true;
    }
}
