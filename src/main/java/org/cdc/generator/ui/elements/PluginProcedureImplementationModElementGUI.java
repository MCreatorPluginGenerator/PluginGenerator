package org.cdc.generator.ui.elements;

import com.google.gson.JsonArray;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.PluginProcedureImplementationModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.factories.AutoCompletionFactory;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PluginProcedureImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<PluginProcedureImplementationModElement> {
    private final VComboBox<String> generator = new VComboBox<>();
    private final VComboBox<String> pluginprocedureElementName = new VComboBox<>();

    private final RSyntaxTextArea content = new RSyntaxTextArea();

    public PluginProcedureImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        if (editingMode) {
            generator.setEnabled(false);
            pluginprocedureElementName.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(2, 2, 5, 5));
        addGeneratorConfiguration(generator);

        pluginprocedureElementName.setEditable(false);
        pluginprocedureElementName.setValidator(new NotEmptyValidator(pluginprocedureElementName::getSelectedItem));
        addElementSelectorConfiguration("pluginprocedure_element_name", pluginprocedureElementName,
                pluginprocedureElementName::getSelectedItem);

        var toolbar = new JToolBar();
        JButton generate = new JButton(UIRES.get("18px.import"));
        generate.setToolTipText("Generate code");
        generate.addActionListener(e -> {
            JsonArray inputs = new JsonArray();
            for (String input : getPluginProcedureModElement().inputs) {
                inputs.add(input);
            }
            JsonArray fields = new JsonArray();
            for (String input : getPluginProcedureModElement().fields) {
                fields.add(input);
            }
            JsonArray statements = new JsonArray();
            for (String statement : getPluginProcedureModElement().statements) {
                statements.add(statement);
            }
            String comment = BuilderUtils.generateInputsComment(inputs) + System.lineSeparator()
                    + BuilderUtils.generateFieldsComment(fields) + System.lineSeparator()
                    + BuilderUtils.generateStatementsComment(statements) + System.lineSeparator();
            content.setText(comment);
        });
        toolbar.add(generate);
        var scrollpane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(content, mcreator);
        AutoCompletionFactory.createDefaultCompletion(content, this::createCompletionProvider);
        var panel = PanelUtils.northAndCenterElement(toolbar, scrollpane);
        panel.setBorder(BorderFactory.createTitledBorder("Body (ctrl+1 to auto complete)"));

        addPage(PanelUtils.northAndCenterElement(configurationPanel, panel));
    }

    private CompletionProvider createCompletionProvider() {
        return new DefaultCompletionProvider();
    }

    @Override protected void openInEditingMode(PluginProcedureImplementationModElement generatableElement) {
        this.generator.setSelectedItem(generatableElement.generator);
        this.pluginprocedureElementName.setSelectedItem(generatableElement.pluginProcedureElementName);
        this.content.setText(generatableElement.content);
    }

    @Override public PluginProcedureImplementationModElement getElementFromGUI() {
        var element = new PluginProcedureImplementationModElement(modElement);
        element.generator = generator.getSelectedItem();
        element.pluginProcedureElementName = pluginprocedureElementName.getSelectedItem();
        element.content = content.getText();
        return element;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.PROCEDURE.getRegistryName())) {
            stringArrayList.add(element.getName());
        }
        ComboBoxUtil.updateComboBoxContents(pluginprocedureElementName, stringArrayList);
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    public PluginProcedureModElement getPluginProcedureModElement() {
        var trigger = mcreator.getWorkspace().getModElementByName(pluginprocedureElementName.getSelectedItem());
        if (trigger.getGeneratableElement() instanceof PluginProcedureModElement pluginProcedure) {
            return pluginProcedure;
        }
        return null;
    }
}
