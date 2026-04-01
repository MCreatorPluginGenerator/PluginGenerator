package org.cdc.generator.ui.elements;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.ProcedureCategoryModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.Rules;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

public class ProcedureCategoryModElementGUI
        extends AbstractConfigurationTableModElementGUI<ProcedureCategoryModElement> {

    // aitasks and so on can extends the class.
    protected final VTextField name;
    protected final VTextField readableName;
    protected final JColor color;
    protected final SearchableComboBox<String> parentCategory;
    protected final VTextField customCategory;
    protected final JCheckBox isApi;

    public ProcedureCategoryModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, null);
        this.name = new VTextField();
        this.readableName = new VTextField();
        this.color = new JColor(mcreator, false, false);
        this.parentCategory = new SearchableComboBox<>();
        this.customCategory = new VTextField();
        this.isApi = createDefaultCheckBox();

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(6, 2, 5, 5));

        name.setText(modElement.getRegistryName());
        name.setValidator(Rules.getFileNameValidator(name::getText));
        addNameConfiguration(name);

        readableName.setText(modElement.getName());
        addConfigurationWithHelpEntry("readable_name", readableName);

        addConfigurationWithHelpEntry("color", color);

        addConfigurationWithHelpEntry("parent_category", parentCategory);

        addConfigurationWithHelpEntry("custom_parent_category", customCategory);

        addConfigurationWithHelpEntry("is_api", isApi);

        addPage("edit", PanelUtils.totalCenterInPanel(configurationPanel));
    }

    @Override protected void openInEditingMode(ProcedureCategoryModElement generatableElement) {
        this.name.setEnabled(false);
        this.readableName.setText(generatableElement.readableName);
        this.color.setColor(generatableElement.color);
        this.parentCategory.setSelectedItem(generatableElement.parentCategory);
        this.isApi.setSelected(generatableElement.api);
    }

    @Override public ProcedureCategoryModElement getElementFromGUI() {
        modElement.setRegistryName(name.getText());
        var element = new ProcedureCategoryModElement(modElement);
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

    @Override @Nullable public URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public void reloadDataLists() {
        var stringArrayList = new HashSet<String>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.PROCEDURE_CATEGORY.getRegistryName())) {
            stringArrayList.add(element.getRegistryName());
        }
        stringArrayList.addAll(BlocklyLoader.getBuiltinCategories());
        BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.PROCEDURE).getDefinedBlocks().values().forEach(a -> {
            if (a.getToolboxCategory() != null) {
                stringArrayList.add(a.getToolboxCategoryRaw());
            }
        });
        ComboBoxUtil.updateComboBoxContents(parentCategory, stringArrayList.stream().sorted().toList());
    }
}
