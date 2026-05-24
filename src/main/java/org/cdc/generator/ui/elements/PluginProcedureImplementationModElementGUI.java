package org.cdc.generator.ui.elements;

import com.google.gson.JsonArray;
import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.PluginProcedureImplementationModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.ElementsUtils;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.factories.AutoCompletionFactory;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.ioc.InjectField;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PluginProcedureImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<PluginProcedureImplementationModElement> {
    final VComboBox<String> generator = new VComboBox<>();
    final VTextField parentFolder = new VTextField();
    final VComboBox<String> procedureFileName = new VComboBox<>();
    final JCheckBox isTemplate = createDefaultCheckBox();

    private final RSyntaxTextArea content = new RSyntaxTextArea();
    private AutoCompletion lastAutoCompletion;

    @InjectField org.apache.logging.log4j.Logger LOG;

    public PluginProcedureImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        if (editingMode && isUnique()) {
            generator.setEnabled(false);
            procedureFileName.setEnabled(false);
            isTemplate.setEnabled(false);
            parentFolder.setEnabled(false);
        }
    }

    @Override public void initAfterAll() {
        this.initGUI();
        this.finalizeGUI();

        procedureFileName.addItemListener(a -> reloadComplete());
        reloadComplete();
    }

    private void reloadComplete() {
        if (lastAutoCompletion != null) {
            lastAutoCompletion.uninstall();
        }
        lastAutoCompletion = AutoCompletionFactory.createDefaultParameterCompletion(content,
                this::createCompletionProvider);
    }

    @Override protected void initGUI() {
        addGeneratorConfiguration(generator);

        parentFolder.setText("procedures");
        addConfigurationWithHelpEntry("parent_folder", parentFolder);

        procedureFileName.setEditable(true);
        procedureFileName.setValidator(Rules.getFileNameValidator(procedureFileName::getSelectedItem));
        procedureFileName.addItemListener(a -> {
            if (a.getStateChange() == ItemEvent.SELECTED) {
                var selected = a.getItem().toString();
                var registry = ElementsUtils.getProcedureFileName(getModElement().getWorkspace(), selected);
                if (registry != null) {
                    procedureFileName.setSelectedItem(registry);
                    if (mcreator.getWorkspace().getModElementByName(selected)
                            .getGeneratableElement() instanceof IBlocklyElement blocklyElement) {
                        parentFolder.setText(blocklyElement.getBlocklyFolder());
                    }
                    LOG.debug("Converted to {}", registry);
                }
            }
        });
        addElementSelectorConfiguration("pluginprocedure_element_name", procedureFileName,
                () -> getPluginProcedureModElement().getModElement());

        addConfigurationWithHelpEntry("is_template", isTemplate);

        var toolbar = new JToolBar();
        JButton generate = new JButton(UIRES.get("18px.import"));
        generate.setToolTipText("Generate code");
        generate.addActionListener(e -> {
            JsonArray inputs = new JsonArray();
            var procedureModElement = getPluginProcedureModElement();
            for (String input : procedureModElement.inputs) {
                inputs.add(input);
            }
            JsonArray fields = new JsonArray();
            for (String input : procedureModElement.fields) {
                fields.add(input);
            }
            JsonArray statements = new JsonArray();
            for (String statement : procedureModElement.statements) {
                statements.add(statement);
            }
            String comment = BuilderUtils.generateInputsComment(inputs) + System.lineSeparator()
                    + BuilderUtils.generateFieldsComment(fields) + System.lineSeparator()
                    + BuilderUtils.generateStatementsComment(statements) + System.lineSeparator();
            content.setText(comment + "\n" + content.getText());
            LOG.debug("Generated procedure impl code: {}", content.getText());
        });
        toolbar.add(generate);

        toolbar.add(syncLocalImplFile(content::setText));
        var scrollpane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(content, mcreator);
        var panel = PanelUtils.northAndCenterElement(toolbar, scrollpane);
        panel.setBorder(BorderFactory.createTitledBorder("Body (ctrl+1 to auto complete)"));

        addPage(PanelUtils.northAndCenterElement(buildConfiguration(2), panel)).validate(generator)
                .validate(procedureFileName);
    }

    private CompletionProvider createCompletionProvider() {
        var complete = new DefaultCompletionProvider();
        var element = getPluginProcedureModElement();
        if (element != null) {
            for (String input : element.inputs) {
                complete.addCompletion(new BasicCompletion(complete, BuilderUtils.getInputPlaceHolder(input)));
                complete.addCompletion(new BasicCompletion(complete, "input$" + input));
            }
            for (String field : element.fields) {
                complete.addCompletion(new BasicCompletion(complete, BuilderUtils.getFieldPlaceHolder(field)));
                complete.addCompletion(new BasicCompletion(complete, "field$" + field));
            }
            for (String statement : element.statements) {
                complete.addCompletion(new BasicCompletion(complete, BuilderUtils.getStatementPlaceHolder(statement)));
                complete.addCompletion(new BasicCompletion(complete, "statement$" + statement));
            }
            for (PluginProcedureModElement.Dependency dependency : element.dependencies) {
                complete.addCompletion(new BasicCompletion(complete, dependency.getName(), dependency.getType()));
            }
        }
        //addTemplate
        for (GeneratableElement generatableElement : mcreator.getWorkspaceInfo()
                .getGElementsOfType(ModElementTypes.PROCEDURE_IMPLEMENTATION.getRegistryName())) {
            if (generatableElement instanceof PluginProcedureImplementationModElement _modelement)
                if (_modelement.isTemplate) {
                    complete.addCompletion(new BasicCompletion(complete,
                            "<@addTemplate file=\"utils/" + _modelement.getProcedureFileName() + ".java.ftl\"/>"));
                }
        }
        complete.addCompletion(new TemplateCompletion(complete, "head", "head", "<@head>${cursor}</@head>"));
        complete.addCompletion(new TemplateCompletion(complete, "tail", "tail", "<@tail>${cursor}</@tail>"));
        complete.addCompletion(new BasicCompletion(complete, "addTemplate"));
        Utils.initCompletionWithGenerator(complete, mcreator.getGenerator());

        return complete;
    }

    @Override protected void openInEditingMode(PluginProcedureImplementationModElement generatableElement) {
        this.generator.setSelectedItem(generatableElement.generator);
        this.procedureFileName.setSelectedItem(generatableElement.procedureFileName);
        this.isTemplate.setSelected(generatableElement.isTemplate);
        this.parentFolder.setText(generatableElement.procedureFolder);

        this.content.setText(generatableElement.content);
    }

    @Override public PluginProcedureImplementationModElement getElementFromGUI() {
        var element = new PluginProcedureImplementationModElement(modElement);
        element.generator = generator.getSelectedItem();
        element.procedureFolder = parentFolder.getText();
        element.procedureFileName = procedureFileName.getSelectedItem();
        element.searchable = getPluginProcedureModElement().getModElement().getName();
        element.content = content.getText();
        element.isTemplate = isTemplate.isSelected();
        return element;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.PROCEDURE.getRegistryName())) {
            stringArrayList.add(element.getName());
        }
        ComboBoxUtil.updateComboBoxContents(procedureFileName, stringArrayList);
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return new URI(
                "https://mcreator.net/wiki/create-new-procedure-blocks#:~:text=0%20and%20360.-,Make%20the%20code%20of%20your%20procedure%20block,-The%20folder");
    }

    public PluginProcedureModElement getPluginProcedureModElement() {
        for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
            if (modElement.getRegistryName().equals(procedureFileName.getSelectedItem())) {
                return (PluginProcedureModElement) modElement.getGeneratableElement();
            }
        }
        return null;
    }
}
