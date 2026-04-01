package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.elements.VariableModElement;
import org.cdc.generator.utils.Constants;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.Utils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class VariableModElementGUI extends AbstractConfigurationTableModElementGUI<VariableModElement> {

    private final JCheckBox generate = L10N.checkbox("elementgui.common.enable");
    private final VComboBox<String> name = new VComboBox<>();

    private final VTextField blocklyVariableType = new VTextField();
    private final JCheckBox ignoredByCoverage = L10N.checkbox("elementgui.common.enable");
    private final JCheckBox nullable = L10N.checkbox("elementgui.common.enable");
    private final JStringListField requiredApis;
    private final JColor color;
    private final VComboBox<String> builtInColor;

    public VariableModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        this.requiredApis = new JStringListField(mcreator, vTextField -> Rules.getModidValidator(vTextField::getText));
        this.color = new JColor(mcreator, false, false);
        this.builtInColor = new VComboBox<>(new String[] { Constants.NONE, Constants.BuiltInColors.BKY_TEXTS_HUE,
                Constants.BuiltInColors.BKY_LOGIC_HUE, Constants.BuiltInColors.BKY_MATH_HUE });
        if (editingMode) {
            name.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(8, 2, 5, 5));

        name.setEditable(true);
        name.setSelectedItem(modElement.getRegistryName());
        name.setValidator(Rules.getFileNameValidator(name::getSelectedItem));
        addNameConfiguration(name);

        generate.setSelected(true);
        addConfigurationWithHelpEntry("generate", generate);

        blocklyVariableType.setOpaque(false);
        blocklyVariableType.setText(modElement.getName());
        addConfigurationWithHelpEntry("blockly_variable_type", blocklyVariableType);

        ignoredByCoverage.setOpaque(false);
        addConfigurationWithHelpEntry("ignored_by_coverage", ignoredByCoverage);

        nullable.setOpaque(false);
        addConfigurationWithHelpEntry("nullable", nullable);

        requiredApis.setOpaque(false);
        addConfigurationWithHelpEntry("required_apis", requiredApis);

        addConfigurationWithHelpEntry("color", color);

        builtInColor.setOpaque(false);
        builtInColor.setEditable(true);
        addConfigurationWithHelpEntry("builtincolor", builtInColor);

        addPage(PanelUtils.totalCenterInPanel(configurationPanel)).validate(name);
    }

    @Override protected void openInEditingMode(VariableModElement generatableElement) {
        this.generate.setSelected(generatableElement.generate);
        this.name.setSelectedItem(generatableElement.name);
        this.blocklyVariableType.setText(generatableElement.blocklyVariableType);
        this.nullable.setSelected(generatableElement.nullable);
        this.ignoredByCoverage.setSelected(generatableElement.ignoredByCoverage);
        this.requiredApis.setTextList(generatableElement.required_apis);
        this.color.setColor(generatableElement.color);
        this.builtInColor.setSelectedItem(Utils.nullToNoneOrNoneToNull(generatableElement.strColor));
    }

    @Override public VariableModElement getElementFromGUI() {
        VariableModElement variableModElement = new VariableModElement(modElement);
        variableModElement.generate = generate.isSelected();
        variableModElement.name = name.getSelectedItem();
        variableModElement.blocklyVariableType = blocklyVariableType.getText();
        variableModElement.nullable = nullable.isSelected();
        variableModElement.ignoredByCoverage = ignoredByCoverage.isSelected();
        variableModElement.required_apis = requiredApis.getTextList();
        variableModElement.color = color.getColor();
        variableModElement.strColor = Utils.nullToNoneOrNoneToNull(builtInColor.getSelectedItem());
        return variableModElement;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> variables = new ArrayList<>();
        for (VariableType allVariableType : VariableTypeLoader.INSTANCE.getAllVariableTypes()) {
            variables.add(allVariableType.getName());
        }
        ComboBoxUtil.updateComboBoxContents(name, variables);
    }
}
