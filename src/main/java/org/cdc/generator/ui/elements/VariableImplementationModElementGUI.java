package org.cdc.generator.ui.elements;

import jdk.jfr.Description;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;
import org.cdc.generator.elements.VariableImplementationModElement;
import org.cdc.generator.elements.VariableModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.ui.SearchableComboBox;
import org.cdc.generator.utils.DialogUtils;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.YamlUtils;
import org.cdc.generator.utils.factories.AutoCompletionFactory;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.ioc.InjectField;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class VariableImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<VariableImplementationModElement> {
    final SearchableComboBox<String> generator = new SearchableComboBox<>();
    final SearchableComboBox<String> variableElementName = new SearchableComboBox<>();
    final VTextField defaultValue = new VTextField();

    private List<VariableImplementationModElement.VariableScope> scopeList = new ArrayList<>();

    private final Map<String, MethodHandle> cacheHandles = new HashMap<>();
    // Access the mcreator private field.
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @InjectField private Logger LOGGER;
    @InjectField private Container container;

    public VariableImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[] { "Scope name", "Init", "Get", "Set", "Read", "Write" });

        if (editingMode && isUnique()) {
            generator.setEnabled(false);
            variableElementName.setEnabled(false);
        }
    }

    @Override public void initAfterAll() {
        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        addGeneratorConfiguration(generator);

        variableElementName.setEditable(false);
        variableElementName.setValidator(new NotEmptyValidator(variableElementName::getSelectedItem));
        variableElementName.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                var element = mcreator.getWorkspace().getModElementByName(Objects.toString(value));
                if (element != null) {
                    if (element.getGeneratableElement() instanceof VariableModElement variableModElement) {
                        jLabel.setIcon(ImageUtils.createColorSquare(variableModElement.color, 32, 32));
                    }
                }
                return jLabel;
            }
        });
        addElementSelectorConfiguration("variable_element_name", variableElementName,
                () -> mcreator.getWorkspace().getModElementByName(variableElementName.getSelectedItem()));

        defaultValue.setText("null");
        var notempty = new NotEmptyValidator(defaultValue::getText);
        defaultValue.setValidator(() -> {
            VariableModElement element = (VariableModElement) mcreator.getWorkspace().getModElementByName(variableElementName.getSelectedItem()).getGeneratableElement();
            if (element != null && defaultValue.getText().equals("null") && !element.nullable) {
                return new ValidationResult(ValidationResult.Type.WARNING,"Your variable is not nullable");
            }
            return notempty.validate();
        });
        defaultValue.setPreferredSize(Utils.tryToGetTextFieldSize());
        defaultValue.enableRealtimeValidation();
        addConfigurationWithHelpEntry("default_value", defaultValue);

        initTable(new ScopesTableModel());
        jTable.setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()) {

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value1, boolean isSelected, int rowIndex,
                    int column) {
                var row = scopeList.get(rowIndex);
                var jTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
                AutoCompletionFactory.createDefaultParameterCompletion(jTextArea,
                        VariableImplementationModElementGUI.this::createCompletionProvider);
                var toolbar = new JToolBar();
                toolbar.setBorder(BorderFactory.createTitledBorder("Examples"));
                var columnName = columns[column];
                container.registerObject("generatorName", generator::getSelectedItem);
                container.registerTemporaryObject("name", row::getName);
                container.registerTemporaryObject("columnName", () -> columnName.toLowerCase(Locale.ROOT));
                IExamplesProvider.examplesProviders.stream().forEach(a -> {
                    if (a.type().isAnnotationPresent(Description.class)) {
                        var des = a.type().getAnnotation(Description.class);
                        if (des.value().equals("VarImplExamples")) {
                            container.inject(a.get())
                                    .provideExamples(toolbar::add, text -> jTextArea.setText(Objects.toString(text)),
                                            new String[] { generator.getSelectedItem(), row.getName(),
                                                    columnName.toLowerCase(Locale.ROOT) });
                        }
                    }
                });
                container.endTemporaryLife();

                int op = DialogUtils.showOptionPaneWithTextAreaAndToolBar(jTextArea, toolbar, mcreator,
                        "Edit" + columnName + " lines (one line one item)",
                        YamlUtils.splitString(Objects.requireNonNullElse(value1, "").toString()));
                if (op == JOptionPane.YES_OPTION) {
                    LOGGER.info("Notify {} has changed to {}", columnName, jTextArea.getText());
                    try {
                        MethodHandle set;
                        if (cacheHandles.containsKey(columnName)) {
                            set = cacheHandles.get(columnName);
                        } else {
                            set = lookup.findVirtual(VariableImplementationModElement.VariableScope.class,
                                    "set" + columnName, MethodType.methodType(Void.TYPE, String.class));
                        }
                        cacheHandles.put(columnName, set);
                        if (jTextArea.getText().isBlank()){
                            set.invoke(row,null);
                        } else {
                            set.invoke(row, jTextArea.getText());
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }
        });
        if (!isEditingMode()) {
            for (String s : Utils.getAllVariableScope()) {
                scopeList.add(new VariableImplementationModElement.VariableScope(s));
            }
        }

        addPage("Configuration", PanelUtils.northAndCenterElement(buildConfiguration(2), wrapTable())).validate(
                variableElementName).validate(generator).validate(defaultValue).lazyValidate(
                () -> scopeList.stream().anyMatch(VariableImplementationModElement.VariableScope::hasNotNull) ?
                        new AggregatedValidationResult.PASS() :
                        new AggregatedValidationResult.FAIL("You should edit at least one scope"));

    }

    @Override protected void openInEditingMode(VariableImplementationModElement generatableElement) {
        this.generator.setSelectedItem(generatableElement.getGeneratorName());
        this.variableElementName.setSelectedItem(generatableElement.variableElementName);
        this.defaultValue.setText(generatableElement.defaultValue);
        this.scopeList.addAll(generatableElement.scopes.stream().map(VariableImplementationModElement.VariableScope::clone).toList());
        var scopes = Utils.getAllVariableScope();
        if (scopeList.size() != scopes.size()){
            var set = scopeList.stream().map(VariableImplementationModElement.VariableScope::getName).collect(Collectors.toSet());
            for (String scope : scopes) {
                if (set.contains(scope)){
                    continue;
                }
                scopeList.add(new VariableImplementationModElement.VariableScope(scope));
            }
        }
    }

    @Override public VariableImplementationModElement getElementFromGUI() {
        VariableImplementationModElement element = new VariableImplementationModElement(modElement);
        element.generator = generator.getSelectedItem();
        element.variableElementName = variableElementName.getSelectedItem();
        element.defaultValue = defaultValue.getText();
        element.scopes = scopeList;
        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return new URI("https://mcreator.net/wiki/creating-new-variable-types#:~:text=false-,Making%20the%20code,-Files%20and%20folders");
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.VARIABLE.getRegistryName())) {
            stringArrayList.add(element.getName());
        }
        ComboBoxUtil.updateComboBoxContents(variableElementName, stringArrayList);
    }

    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.addCompletion(new BasicCompletion(provider, "${name", "the name of variable"));
        provider.addCompletion(new BasicCompletion(provider, "${scope", "the scope of variable"));
        provider.addCompletion(new BasicCompletion(provider, "${type", "the type of variable"));
        provider.addCompletion(new BasicCompletion(provider, "${value", "the value of variable"));
        provider.addCompletion(new BasicCompletion(provider, "${entity", "the entity of variable (nullable)"));
        provider.addCompletion(
                new BasicCompletion(provider, "${var", "net.mcreator.workspace.elements.VariableElement"));

        Utils.initCompletionWithGenerator(provider, mcreator.getGenerator());

        return provider;
    }

    private class ScopesTableModel extends AbstractTableModel {

        @Override public int getRowCount() {
            return scopeList.size();
        }

        @Override public int getColumnCount() {
            return columns.length;
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            var row = scopeList.get(rowIndex);
            var columns = new String[] { row.getName(), row.getInit(), row.getGet(), row.getSet(), row.getRead(),
                    row.getWrite() };
            return columns[columnIndex];
        }

        @Override public String getColumnName(int column) {
            return columns[column];
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            return !columns[columnIndex].equals("Scope name");
        }
    }
}
