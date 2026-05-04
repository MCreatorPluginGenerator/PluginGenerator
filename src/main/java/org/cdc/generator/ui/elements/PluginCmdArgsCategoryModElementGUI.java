package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginCmdArgsCategoryModElement;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.jspecify.annotations.NonNull;

public class PluginCmdArgsCategoryModElementGUI extends AbstractProcedureCategoryModElementGUI<PluginCmdArgsCategoryModElement>{
    public PluginCmdArgsCategoryModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);

        initGUI();
        finalizeGUI();
    }

    @Override public BlocklyEditorType getBlocklyEditorType() {
        return BlocklyEditorType.COMMAND_ARG;
    }

    @Override public Class<? extends IBlocklyCategoryElement> getBlocklyCategoryClass() {
        return PluginCmdArgsCategoryModElement.class;
    }

    @Override protected void openInEditingMode(PluginCmdArgsCategoryModElement generatableElement) {
        super.openInEditingMode0(generatableElement);
    }

    @Override public PluginCmdArgsCategoryModElement getElementFromGUI() {
        modElement.setRegistryName(name.getText());
        var element = new PluginCmdArgsCategoryModElement(modElement);
        element.readableName = readableName.getText();
        if (customCategory.getText() != null && !customCategory.getText().isBlank()) {
            element.parentCategory = customCategory.getText();
        } else {
            element.parentCategory = parentCategory.getSelectedItem();
        }
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
