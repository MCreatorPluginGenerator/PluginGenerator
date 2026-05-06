package org.cdc.generator.ui.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.ProcedureCategoryModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.utils.Constants;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.Utils;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractProcedureCategoryModElementGUI<E extends GeneratableElement & IBlocklyElement>
        extends AbstractConfigurationTableModElementGUI<E> implements IListBlocklyCategoriesModElementGUI {

    // aitasks and so on can extends the class.
    protected final VTextField name;
    protected final VTextField readableName;
    protected final JColor color;
    protected final SearchableComboBox<String> parentCategory;
    protected final VTextField customCategory;
    protected final JCheckBox isApi;

    public AbstractProcedureCategoryModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode, null);
        this.name = new VTextField();
        this.readableName = new VTextField();
        this.color = new JColor(mcreator, false, false);
        this.parentCategory = new SearchableComboBox<>();
        this.customCategory = new VTextField();
        this.isApi = createDefaultCheckBox();
    }

    @Override protected void initGUI() {
        name.setText(modElement.getRegistryName());
        name.setValidator(Rules.getFileNameValidator(name::getText));
        addNameConfiguration(name);

        readableName.setText(modElement.getName());
        addConfigurationWithHelpEntry("readable_name", readableName);

        addConfigurationWithHelpEntry("color", color);

        parentCategory.setSelectedItem(Constants.NONE);
        addConfigurationWithHelpEntry("parent_category", parentCategory);

        addConfigurationWithHelpEntry("custom_parent_category", customCategory);

        addConfigurationWithHelpEntry("is_api", isApi);

        addPage("edit", PanelUtils.totalCenterInPanel(buildConfiguration(2))).validate(name);
    }

    protected void openInEditingMode0(ProcedureCategoryModElement generatableElement) {
        this.name.setEnabled(false);
        this.readableName.setText(generatableElement.readableName);
        this.color.setColor(generatableElement.color);
        this.parentCategory.setSelectedItem(generatableElement.parentCategory);
        this.isApi.setSelected(generatableElement.api);
    }

    @Override @Nullable public URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public void reloadDataLists() {
        var stringArrayList = Utils.getAllCategories(mcreator, getBlocklyEditorType(), getBlocklyCategoryClass(),
                hasBuiltinCategories());
        stringArrayList.add(Constants.NONE);
        ComboBoxUtil.updateComboBoxContents(parentCategory, stringArrayList.stream().sorted().toList());
    }
}
