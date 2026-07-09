package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginAITasksCategoryModElement;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.jspecify.annotations.NonNull;

public class PluginAITasksCategoryModElementGUI
        extends AbstractProcedureCategoryModElementGUI<PluginAITasksCategoryModElement> {
    public PluginAITasksCategoryModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);

        initGUI();
        finalizeGUI();
    }

    @Override protected void initGUI() {
        super.initGUI();

        addPage("edit", PanelUtils.totalCenterInPanel(buildConfiguration(2))).validate(name);
    }

    @Override public BlocklyEditorType getBlocklyEditorType() {
        return BlocklyEditorType.AI_TASK;
    }

    @Override public Class<? extends IBlocklyCategoryElement> getBlocklyCategoryClass() {
        return PluginAITasksCategoryModElement.class;
    }

    @Override public boolean hasBuiltinCategories() {
        return false;
    }

    @Override protected void openInEditingMode(PluginAITasksCategoryModElement generatableElement) {
        super.openInEditingMode0(generatableElement);
    }

    @Override public PluginAITasksCategoryModElement getElementFromGUI() {
        modElement.setRegistryName(name.getText());
        var element = new PluginAITasksCategoryModElement(modElement);
        element.readableName = readableName.getText();
        element.parentCategory = getParentCategory();
        element.color = color.getColor();
        element.api = isApi.isSelected();
        return element;
    }

    @Override protected String getHelpEntryAndLocalizationPrefix() {
        return "pluginprocedurecategory";
    }
}
