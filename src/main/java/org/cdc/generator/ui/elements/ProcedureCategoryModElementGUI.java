package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.ProcedureCategoryModElement;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;
import org.cdc.generator.utils.Utils;
import org.jspecify.annotations.NonNull;

public class ProcedureCategoryModElementGUI
        extends AbstractProcedureCategoryModElementGUI<ProcedureCategoryModElement> {

    public ProcedureCategoryModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode);

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        super.initGUI();

        addPage("edit", PanelUtils.totalCenterInPanel(buildConfiguration(2))).validate(name);
    }

    @Override protected void openInEditingMode(ProcedureCategoryModElement generatableElement) {
        super.openInEditingMode0(generatableElement);
    }

    @Override public ProcedureCategoryModElement getElementFromGUI() {
        modElement.setRegistryName(name.getText());
        var element = new ProcedureCategoryModElement(modElement);
        element.readableName = readableName.getText();
        if (customCategory.getText() != null && !customCategory.getText().isBlank()) {
            element.parentCategory = customCategory.getText();
        } else {
            element.parentCategory = Utils.nullToNoneOrNoneToNull(parentCategory.getSelectedItem(), false);
        }
        element.color = color.getColor();
        element.api = isApi.isSelected();
        return element;
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
