package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.utils.Rules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;

public class PluginProceduresElementGUI extends AbstractConfigurationTableModElementGUI<PluginProcedureModElement> {

    protected final VTextField name = new VTextField();
    protected final JCheckBox inputsInline;
    protected final VTextField previousStatement = new VTextField();
    protected final VTextField nextStatement = new VTextField();
    protected final JColor color;
    protected final JStringListField outputs;
    protected final VComboBox<String> toolboxId = new VComboBox<>();
    protected final VTextField group = new VTextField();
    protected final JStringListField warnings;
    protected final JStringListField requiredApis;

    public PluginProceduresElementGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[] {});
        this.inputsInline = createDefaultCheckBox();
        this.color = new JColor(mcreator, false, false);
        this.outputs = new JStringListField(mcreator, a -> Rules.getFileNameValidator(a::getText));
        this.warnings = new JStringListField(mcreator, null).setUniqueEntries(true);
        this.requiredApis = new JStringListField(mcreator, a -> Rules.getFileNameValidator(a::getText));

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {

    }

    @Override protected void openInEditingMode(PluginProcedureModElement generatableElement) {
        this.inputsInline.setSelected(generatableElement.inputsInline);
        this.previousStatement.setText(generatableElement.previousStatement);
        this.nextStatement.setText(generatableElement.nextStatement);
        this.color.setColor(generatableElement.colour);
        this.outputs.setTextList(generatableElement.outputs);
        this.toolboxId.setSelectedItem(generatableElement.toolbox_id);
        this.group.setText(generatableElement.group);
        this.warnings.setTextList(generatableElement.warnings);
        this.requiredApis.setTextList(generatableElement.required_apis);
    }

    @Override public PluginProcedureModElement getElementFromGUI() {
        this.modElement.setRegistryName(name.getText());
        var element = new PluginProcedureModElement(modElement);
        element.inputsInline = this.inputsInline.isSelected();
        element.previousStatement = this.previousStatement.getText();
        element.nextStatement = this.nextStatement.getText();
        element.colour = this.color.getColor();
        element.outputs = this.outputs.getTextList();
        element.toolbox_id = this.toolboxId.getSelectedItem();
        element.group = this.group.getText();
        element.warnings = this.warnings.getTextList();
        element.required_apis = this.requiredApis.getTextList();
        return element;
    }

    @Override @Nullable public URI contextURL() throws URISyntaxException {
        return null;
    }
}
