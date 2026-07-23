package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginCustomCategoryModElement;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.jspecify.annotations.NonNull;

import javax.swing.*;

public class PluginCustomCategoryModElementGUI
        extends AbstractProcedureCategoryModElementGUI<PluginCustomCategoryModElement> {

    private JTextField parentFolder = new JTextField();

    public PluginCustomCategoryModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);

        if (editingMode) {
            parentFolder.setEnabled(false);
        }

        initGUI();
        finalizeGUI();
    }

    @Override protected void initGUI() {
        parentFolder.setText("procedures");
        addConfigurationWithHelpEntry("parent_folder", parentFolder);

        super.initGUI();

        addPage("edit", PanelUtils.totalCenterInPanel(buildConfiguration(2))).validate(name);
    }

    @Override public BlocklyEditorType getBlocklyEditorType() {
        return BlocklyEditorType.COMMAND_ARG;
    }

    @Override public Class<? extends IBlocklyCategoryElement> getBlocklyCategoryClass() {
        return PluginCustomCategoryModElement.class;
    }

    @Override protected void openInEditingMode(PluginCustomCategoryModElement generatableElement) {
        super.openInEditingMode0(generatableElement);
        this.parentFolder.setText(generatableElement.parentFolder);
    }

    @Override public PluginCustomCategoryModElement getElementFromGUI() {
        modElement.setRegistryName(name.getText());
        var element = new PluginCustomCategoryModElement(modElement);
        element.parentFolder = this.parentFolder.getText();
        element.readableName = readableName.getText();
        element.parentCategory = getParentCategory();
        element.color = color.getColor();
        element.api = isApi.isSelected();
        return element;
    }

    @Override protected String getHelpEntryAndLocalizationPrefix() {
        return "pluginprocedurecategory";
    }

    @Override public boolean hasBuiltinCategories() {
        return false;
    }
}
