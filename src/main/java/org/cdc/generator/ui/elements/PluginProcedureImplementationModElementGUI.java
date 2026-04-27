package org.cdc.generator.ui.elements;

import com.google.gson.JsonArray;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.PluginProcedureImplementationModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.ElementsUtils;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.factories.AutoCompletionFactory;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.ioc.InjectField;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PluginProcedureImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<PluginProcedureImplementationModElement> {
    private final VComboBox<String> generator = new VComboBox<>();
    private final VComboBox<String> procedureFileName = new VComboBox<>();

    private final RSyntaxTextArea content = new RSyntaxTextArea();
    private AutoCompletion lastAutoCompletion;

    @InjectField org.apache.logging.log4j.Logger LOG;

    public PluginProcedureImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        if (editingMode && isUnique()) {
            generator.setEnabled(false);
            procedureFileName.setEnabled(false);
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
        lastAutoCompletion = AutoCompletionFactory.createDefaultCompletion(content, this::createCompletionProvider);
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(2, 2, 5, 5));
        addGeneratorConfiguration(generator);

        procedureFileName.setEditable(true);
        procedureFileName.setValidator(Rules.getFileNameValidator(procedureFileName::getSelectedItem));
        procedureFileName.addItemListener(a -> {
            if (a.getStateChange() == ItemEvent.SELECTED) {
                var registry = ElementsUtils.getProcedureFileName(getModElement().getWorkspace(),
                        a.getItem().toString());
                if (registry != null) {
                    procedureFileName.setSelectedItem(registry);
                }
            }
        });
        addElementSelectorConfiguration("pluginprocedure_element_name", procedureFileName,
                () -> getPluginProcedureModElement().getModElement());

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
            content.setText(comment);
        });
        toolbar.add(generate);
        var scrollpane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(content, mcreator);
        var panel = PanelUtils.northAndCenterElement(toolbar, scrollpane);
        panel.setBorder(BorderFactory.createTitledBorder("Body (ctrl+1 to auto complete)"));

        addPage(PanelUtils.northAndCenterElement(configurationPanel, panel)).validate(generator).lazyValidate(() ->
                isUnique() ?
                        new AggregatedValidationResult.PASS() :
                        new AggregatedValidationResult.FAIL(L10N.t("warnings.should_be_unique")));
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
        }
        Utils.initCompletionWithGenerator(complete, mcreator.getGenerator());

        return complete;
    }

    @Override protected void openInEditingMode(PluginProcedureImplementationModElement generatableElement) {
        this.generator.setSelectedItem(generatableElement.generator);
        this.procedureFileName.setSelectedItem(generatableElement.procedureFileName);

        this.content.setText(generatableElement.content);
    }

    @Override public PluginProcedureImplementationModElement getElementFromGUI() {
        var element = new PluginProcedureImplementationModElement(modElement);
        element.generator = generator.getSelectedItem();
        element.procedureFileName = procedureFileName.getSelectedItem();
        element.content = content.getText();
        return element;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.PROCEDURE.getRegistryName())) {
            stringArrayList.add(element.getName());
        }
        ComboBoxUtil.updateComboBoxContents(procedureFileName, stringArrayList);
        if (!isEditingMode()) {
            procedureFileName.setSelectedIndex(stringArrayList.size() - 1);
        }
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
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
