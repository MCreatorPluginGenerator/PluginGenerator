package org.cdc.generator.ui.elements;

import com.google.gson.JsonArray;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.PluginProcedureImplementationModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.ui.SearchableComboBox;
import org.cdc.generator.utils.ComboBoxUtil;
import org.cdc.generator.utils.FTLUtils;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Supplier;

public class PluginProcedureImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<PluginProcedureImplementationModElement>
        implements IFreemakerDebugger {
    final SearchableComboBox<String> generator = new SearchableComboBox<>();
    final VTextField parentFolder = new VTextField();
    final SearchableComboBox<String> procedureFileName = new SearchableComboBox<>();
    final JCheckBox isTemplate = createDefaultCheckBox();
    final JTextField templateFolder = new JTextField();
    final JCheckBox debuged = new JCheckBox("debuged");

    private final RSyntaxTextArea content = new RSyntaxTextArea();
    private RSyntaxTextArea relatedSource;
    private AutoCompletion lastAutoCompletion;

    @InjectField org.apache.logging.log4j.Logger LOG;

    private MCreator selectedGeneratorMCreator;

    public PluginProcedureImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        if (editingMode && isUnique()) {
            generator.setEnabled(false);
            procedureFileName.setEnabled(false);
            isTemplate.setEnabled(false);
            parentFolder.setEnabled(false);
            templateFolder.setEnabled(false);
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
            if (a.getStateChange() == ItemEvent.SELECTED && procedureFileName.isPopupVisible()) {
                IBlocklyElement blocklyElement = (IBlocklyElement) getPluginProcedureModElement().get();
                parentFolder.setText(blocklyElement.getBlocklyFolder());
                LOG.debug("Select folder {}", parentFolder.getText());
            }
        });
        addElementSelectorConfiguration("pluginprocedure_element_name", procedureFileName,
                () -> getPluginProcedureModElement().orElseThrow().getModElement());

        addConfigurationWithHelpEntry("is_template", isTemplate);
        isTemplate.addActionListener(a -> {
            templateFolder.setEnabled(isTemplate.isSelected());
        });
        templateFolder.setEnabled(false);
        addConfigurationWithHelpEntry("template_folder", templateFolder);

        var toolbar = new JToolBar();
        JButton generate = new JButton(UIRES.get("18px.import"));
        generate.setToolTipText("Generate code");
        generate.addActionListener(e -> {
            JsonArray inputs = new JsonArray();
            getPluginProcedureModElement().ifPresent(procedureModElement -> {
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
                var text = content.getText();
                if (!text.endsWith(";") && !text.startsWith("(")) {
                    text = "(" + text + ")";
                }
                content.setText(comment + "\n" + text);
                LOG.debug("Generated procedure impl code: {}", content.getText());
            });
        });
        toolbar.add(generate);

        content.setSyntaxEditingStyle("text/java");
        content.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {
                debuged.setSelected(false);
            }

            @Override public void removeUpdate(DocumentEvent e) {
                debuged.setSelected(false);
            }

            @Override public void changedUpdate(DocumentEvent e) {
                debuged.setSelected(false);
            }
        });

        toolbar.add(syncLocalImplFile(content::setText));
        var scrollpane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(content, mcreator);
        var panel = PanelUtils.northAndCenterElement(toolbar, scrollpane);
        panel.setBorder(BorderFactory.createTitledBorder(
                "Body (ctrl+1 to auto complete) You must complete the inputs and fields"));

        addPage(PanelUtils.northAndCenterElement(buildConfiguration(2), panel)).validate(generator)
                .validate(procedureFileName);

        relatedSource = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
        relatedSource.setSyntaxEditingStyle("text/java");
        var scrollPanelForSource = RSyntaxTextAreaFactory.createDefaultTextScrollPane(relatedSource, mcreator);

        scrollPanelForSource.setBorder(BorderFactory.createTitledBorder("You can give the source"));

        addPage("Related source", scrollPanelForSource);

        addPage("Debbuger", getDebuggerComponent(mcreator)).lazyValidate(() -> debuged.isSelected() ?
                new AggregatedValidationResult.PASS() :
                new AggregatedValidationResult.FAIL("You must use debugger and checked the debuged"));
    }

    private CompletionProvider createCompletionProvider() {
        var complete = new DefaultCompletionProvider();
        getPluginProcedureModElement().ifPresent(element -> {
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
        });

        //addTemplate
        for (GeneratableElement generatableElement : mcreator.getWorkspaceInfo()
                .getGElementsOfType(ModElementTypes.PROCEDURE_IMPLEMENTATION.getRegistryName())) {
            if (generatableElement instanceof PluginProcedureImplementationModElement _modelement)
                if (_modelement.isTemplate && Objects.equals(_modelement.generator, generator.getSelectedItem())) {
                    complete.addCompletion(new BasicCompletion(complete,
                            "<@addTemplate file=\"" + _modelement.getCombinedProcedureFolder() + "/"
                                    + _modelement.getProcedureFileName() + ".java.ftl\"/>"));
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
        this.templateFolder.setText(generatableElement.templateFolder);
        this.content.setText(generatableElement.content);
        this.relatedSource.setText(generatableElement.relatedSource);
        this.debuged.setSelected(generatableElement.debugd);
    }

    @Override public PluginProcedureImplementationModElement getElementFromGUI() {
        var element = new PluginProcedureImplementationModElement(modElement);
        element.generator = generator.getSelectedItem();
        element.procedureFolder = parentFolder.getText();
        element.procedureFileName = procedureFileName.getSelectedItem();
        getPluginProcedureModElement().ifPresent(pluginProcedureModElement -> {
            element.searchable = pluginProcedureModElement.getModElement().getName();
        });
        element.content = content.getText();
        element.isTemplate = isTemplate.isSelected();
        element.templateFolder = templateFolder.getText();
        element.relatedSource = relatedSource.getText();
        element.debugd = debuged.isSelected();
        return element;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspace().getModElements()) {
            if (element.getGeneratableElement() instanceof PluginProcedureModElement) {
                stringArrayList.add(element.getRegistryName());
            }
        }
        ComboBoxUtil.updateComboBoxContents(procedureFileName, stringArrayList);
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return new URI(
                "https://mcreator.net/wiki/create-new-procedure-blocks#:~:text=0%20and%20360.-,Make%20the%20code%20of%20your%20procedure%20block,-The%20folder");
    }

    public Optional<PluginProcedureModElement> getPluginProcedureModElement() {
        if (procedureFileName.getSelectedItem() == null) {
            return null;
        }
        for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
            if (modElement.getRegistryName().equals(procedureFileName.getSelectedItem())) {
                return Optional.ofNullable((PluginProcedureModElement) modElement.getGeneratableElement());
            }
        }
        LOG.error("Can not find procedure element {}", procedureFileName.getSelectedItem());
        return Optional.empty();
    }

    @Override public TemplateGenerator getTemplateGenerator() {
        findGeneratorMCreator();
        if (selectedGeneratorMCreator != null) {
            return selectedGeneratorMCreator.getGenerator().getTemplateGeneratorFromName(parentFolder.getText());
        }
        return null;
    }

    private final String METHOD_PARAMETER_KEY = "debugger.method.parameters";

    @Override
    public void modifyToolBar(MCreator mCreator, JToolBar toolBar, JTextArea propertiesTextArea, JTextArea result,
            Supplier<MCreator> mCreatorSupplier) {
        IFreemakerDebugger.super.modifyToolBar(mCreator, toolBar, propertiesTextArea, result,
                () -> selectedGeneratorMCreator);

        toolBar.add(debuged);
    }

    private void findGeneratorMCreator() {
        for (MCreator openMCreator : mcreator.getApplication().getOpenMCreators()) {
            if (openMCreator.getGenerator().getGeneratorName().equals(generator.getSelectedItem())) {
                selectedGeneratorMCreator = openMCreator;
            }
        }
    }

    @Override public String getFreemakerContent(Properties properties) {
        return getFreemakerContent0(properties.getProperty(METHOD_PARAMETER_KEY));
    }

    public String getFreemakerContent0(String parms) {
        if (FTLUtils.isInputProcedure(content.getText())) {
            return """
                    public class ExampleClass{
                        // This a example code. Do not use it.
                        public static Object execute(%s){
                            return %s;
                        }
                    }
                    """.formatted(parms, content.getText());
        }

        return """
                public class ExampleClass{
                    /*This is a example code. Do not use it*/
                    public static void execute(%s){
                    %s
                    }
                }
                """.formatted(parms, content.getText());
    }

    @Override public Properties getDefaultParametersProperties() {
        var properties = new Properties();

        StringBuilder str = new StringBuilder();
        getPluginProcedureModElement().ifPresent(element -> {
            var typeMapping = selectedGeneratorMCreator.getGenerator().getMappings().getMapping("types");
            for (String input : element.inputs) {
                properties.setProperty("input$" + input, input);
                str.append(", Object ").append(input);
            }
            for (String field : element.fields) {
                properties.setProperty("field$" + field, field);
                str.append(", Object ").append(field);
            }
            for (String statement : element.statements) {
                properties.setProperty("statement$" + statement, statement+";//This is a new line");
            }
            for (PluginProcedureModElement.Dependency dependency : element.dependencies) {
                str.append(", ").append(typeMapping.get(dependency.getType())).append(" ").append(dependency.getName());
            }
        });
        properties.setProperty(METHOD_PARAMETER_KEY, "Event event" + str);
        return properties;
    }

    @Override public Map<String, Object> getDefaultParameterMap() {
        var dataModel = new HashMap<String, Object>();
        dataModel.put("cbi", Math.round(Math.random() * 100));
        dataModel.put("addTemplate", new CallPrinter("addTemplate", mcreator));
        dataModel.put("addAdditionalCode", new CallPrinter("addAdditionalCode", mcreator));
        return dataModel;
    }

    private record CallPrinter(String name, MCreator mCreator) implements TemplateDirectiveModel {

        @Override public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
                throws TemplateModelException {
            mCreator.getGradleConsole().append("\n" + name + ": " + params.toString());
        }

    }

    @Override public JScrollPane getResultArea() {
        var rsy = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
        rsy.setSyntaxEditingStyle("text/java");
        return RSyntaxTextAreaFactory.createDefaultTextScrollPane(rsy, this);
    }
}
