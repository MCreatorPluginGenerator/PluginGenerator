package org.cdc.generator.ui.elements;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.ArrayListListModel;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.services.types.ArgTypeProxy;
import org.cdc.generator.ui.APIListField;
import org.cdc.generator.ui.TypeListField;
import org.cdc.generator.utils.*;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.interfaces.IArg0Type;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractProceduresModElementGUI<E extends GeneratableElement & IBlocklyElement>
        extends AbstractConfigurationTableModElementGUI<E> implements ISearchable, IListBlocklyCategoriesModElementGUI {

    protected final VTextField name = new VTextField();
    protected final JCheckBox inputsInline;
    protected final VTextField previousStatement = new VTextField();
    protected final VTextField nextStatement = new VTextField();
    protected final JColor color;
    protected final VComboBox<String> builtInColor;
    protected final VTextField mutator;
    protected final TypeListField outputs;
    protected final JStringListField extensions;
    protected final VComboBox<String> toolboxId = new VComboBox<>();
    protected final VTextField group = new VTextField();
    protected final JStringListField warnings;
    protected final APIListField requiredApis;
    protected final JStringListField inputs;
    protected final JStringListField fields;
    protected final JStringListField statements;
    protected final JStringListField toolboxInit;
    protected final VTextField localizationValue;
    protected final VTextField tooltip;

    protected final ArrayListListModel<ArgTypeProxy> model;
    public JList<ArgTypeProxy> arg0List;

    public List<PluginProcedureModElement.Dependency> dependencies;
    protected JToolBar args0ToolBar;
    protected JSplitPane splitPane;
    protected JToolBar dependenciesToolBar;

    public AbstractProceduresModElementGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[] { "Dependency name", "Type" });
        this.inputsInline = createDefaultCheckBox();
        this.color = new JColor(mcreator, false, false);
        this.builtInColor = new VComboBox<>(Utils.getAllBuiltinColors());
        this.mutator = new VTextField();
        this.outputs = new TypeListField(mcreator, VariableType::blocklyTypeName);
        this.extensions = new JStringListField(mcreator, null);
        this.warnings = new JStringListField(mcreator, null).setUniqueEntries(true);
        this.requiredApis = new APIListField(mcreator);
        this.model = new ArrayListListModel<>();
        this.arg0List = new JList<>(model);
        this.inputs = new JStringListField(mcreator, null);
        this.fields = new JStringListField(mcreator, null);
        this.statements = new JStringListField(mcreator, null);
        this.toolboxInit = new JStringListField(mcreator, vTextField -> () -> {
            if (vTextField.getText().startsWith("<value")) {
                return ValidationResult.PASSED;
            }
            return new ValidationResult(ValidationResult.Type.ERROR, "Must starts with <value");
        });
        this.localizationValue = new VTextField();
        this.tooltip = new VTextField();

        this.dependencies = new ArrayList<>();

        tableTitle = "Dependencies";

        if (editingMode) {
            name.setEnabled(false);
        }
    }

    @Override protected void initGUI() {
        name.setText(modElement.getRegistryName());
        name.setValidator(Rules.getFileNameValidator(name::getText));
        addNameConfiguration(name);

        inputsInline.setSelected(true);
        addConfigurationWithHelpEntry("inputs_inline", inputsInline);
        previousStatement.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && previousStatement.getText().isBlank()) {
                    previousStatement.setText("null");
                }
            }
        });
        addConfigurationWithHelpEntry("previous_statement", previousStatement);
        nextStatement.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && nextStatement.getText().isBlank()) {
                    nextStatement.setText("null");
                }
            }
        });
        addConfigurationWithHelpEntry("next_statement", nextStatement);

        addConfigurationWithHelpEntry("color", color);
        builtInColor.setOpaque(false);
        builtInColor.setEditable(true);
        addConfigurationWithHelpEntry("builtincolor", builtInColor);

        addConfigurationWithHelpEntry("mutator", mutator);

        addConfigurationWithHelpEntry("outputs", outputs);
        addConfigurationWithHelpEntry("extensions", extensions);

        toolboxId.setEditable(true);
        toolboxId.setSelectedItem("other");
        addConfigurationWithHelpEntry("toolbox_id", toolboxId);

        group.setValidator(new NotEmptyValidator(group::getText));
        addConfigurationWithHelpEntry("group", group);

        addConfigurationWithHelpEntry("warnings", warnings);
        addConfigurationWithHelpEntry("required_apis", requiredApis);
        addConfigurationWithHelpEntry("inputs", inputs);
        addConfigurationWithHelpEntry("fields", fields);
        addConfigurationWithHelpEntry("statements", statements);

        JButton openProcedure = new JButton("P");
        openProcedure.setToolTipText("Open a procedure");
        openProcedure.addActionListener(a -> {
            ModElement modElement1 = new ModElement(mcreator.getWorkspace(), "Null", ModElementType.PROCEDURE);
            ModElementType.PROCEDURE.getModElementGUI(mcreator, modElement1, false).showView();
        });
        addConfigurationWithHelpEntry("toolbox_init", PanelUtils.centerAndEastElement(toolboxInit, openProcedure));
        localizationValue.setValidator(() -> {
            var count = BuilderUtils.countLanguageParameterCount(localizationValue.getText());
            if (count < model.size()) {
                return new ValidationResult(ValidationResult.Type.ERROR, "\" " + localizationValue.getText()
                        + " \"is a irregular content because we need parameter count: " + model.size());
            } else if (count > model.size()) {
                return new ValidationResult(ValidationResult.Type.WARNING,
                        "We expect " + model.size() + " but have " + count);
            }
            return ValidationResult.PASSED;
        });
        localizationValue.enableRealtimeValidation();
        addConfigurationWithHelpEntry("localization_value", localizationValue);

        tooltip.setText("Practice makes perfect");
        addConfigurationWithHelpEntry("tooltip", tooltip);

        var typeComboBox = new VComboBox<String>();
        typeComboBox.setOpaque(false);
        typeComboBox.setEditable(true);

        initTable(new DependenciesTableModule());
        jTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                var label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setForeground(Theme.current().getForegroundColor());
                if (columns[column].equals("Type")) {
                    if (VariableTypeLoader.INSTANCE.doesVariableTypeExist(label.getText())) {
                        label.setForeground(VariableTypeLoader.INSTANCE.fromName(label.getText()).getBlocklyColor());
                    }
                }
                return label;
            }
        });
        jTable.setDefaultEditor(String.class, new DefaultCellEditor(typeComboBox) {

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
                    int columnIndex) {
                var columnName = columns[columnIndex];
                typeComboBox.removeAllItems();
                if (columnName.equals("Type")) {
                    for (VariableType supportedType : ElementsUtils.getAllSupportedVariableTypes()) {
                        typeComboBox.addItem(supportedType.name());
                    }
                }
                return super.getTableCellEditorComponent(table, value, isSelected, rowIndex, columnIndex);
            }
        });

        dependenciesToolBar = new JToolBar();
        dependenciesToolBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        dependenciesToolBar.setFloatable(false);
        dependenciesToolBar.setOpaque(false);

        JButton addrow = createJTableAddButton();
        dependenciesToolBar.add(addrow);
        JButton remrow = createJTableRemoveRowButton();
        dependenciesToolBar.add(remrow);

        addrow.addActionListener(a -> {
            dependencies.add(new PluginProcedureModElement.Dependency("name" + dependencies.size(), "type"));
            refreshTable();
        });
        remrow.addActionListener(a -> {
            jTable.editCellAt(-1, 0);
            var stack = new Stack<Integer>();
            Arrays.stream(jTable.getSelectedRows()).forEach(stack::add);
            while (!stack.empty()) {
                dependencies.remove((int) stack.pop());
            }
            refreshTable();
        });

        args0ToolBar = new JToolBar();

        JButton addLine = createAddButton();
        JButton removeLine = createRemoveRowButton();
        JButton importArg0JsonArrary = L10N.button("import arg0 array");

        splitPane = new JSplitPane();
        arg0List.setBorder(BorderFactory.createTitledBorder("List"));
        arg0List.setOpaque(false);
        arg0List.setVisibleRowCount(20);
        arg0List.setMinimumSize(new Dimension(200, 200));
        arg0List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu functions = new JPopupMenu();
        JMenuItem convertCopiedInitValue = new JMenuItem("Copy toolbox init value");
        convertCopiedInitValue.setToolTipText("In procedure's popupmenu, there has a new item named Copy As XML.");
        functions.add(convertCopiedInitValue);
        JMenuItem copyPlaceHolder = new JMenuItem("Copy localization placeholder");
        functions.add(copyPlaceHolder);

        arg0List.setComponentPopupMenu(functions);
        splitPane.setLeftComponent(new JScrollPane(arg0List));
        JPanel rightComponent = new JPanel(new BorderLayout());
        rightComponent.setBorder(BorderFactory.createTitledBorder("Config"));
        splitPane.setRightComponent(rightComponent);
        args0ToolBar.add(addLine);
        convertCopiedInitValue.addActionListener(e -> {
            if (arg0List.getSelectedValue() != null) {
                var str = JOptionPane.showInputDialog(mcreator, "wrap your copied procedure xml or null");
                var content = new StringSelection(
                        "<value name=\"" + arg0List.getSelectedValue().getUniqueName() + "\">" + str + "</value>");
                arg0List.getToolkit().getSystemClipboard().setContents(content, content);
            }
        });
        copyPlaceHolder.addActionListener(e -> {
            if (arg0List.getSelectedValue() != null) {
                var content = new StringSelection("%" + (arg0List.getSelectedIndex() + 1));
                arg0List.getToolkit().getSystemClipboard().setContents(content, content);
            }
        });
        addLine.addActionListener(a -> {
            var json = new JsonObject();
            json.addProperty("type", "input_dummy");
            model.add(new ArgTypeProxy(json));
        });
        removeLine.addActionListener(a -> model.remove(arg0List.getSelectedValue()));
        importArg0JsonArrary.addActionListener(a -> {
            RSyntaxTextArea rSyntaxTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
            var i = DialogUtils.showOptionPaneWithTextArea(rSyntaxTextArea, mcreator, "Input your json array",
                    Collections.emptyList());
            if (i == JOptionPane.YES_OPTION) {
                JsonArray jsonElements = new Gson().fromJson(rSyntaxTextArea.getText(), JsonArray.class);
                for (JsonElement jsonElement : jsonElements) {
                    model.add(new ArgTypeProxy(jsonElement.getAsJsonObject()));
                }
            }
        });
        args0ToolBar.add(addLine);
        args0ToolBar.add(removeLine);
        args0ToolBar.add(importArg0JsonArrary);
        arg0List.addListSelectionListener(listSelectionEvent -> reloadComponent(rightComponent));
        arg0List.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                label.setText((index + 1) + ": " + value);
                return label;
            }
        });

    }

    protected abstract Container getContainer();

    private void reloadComponent(JPanel rightComponent) {
        rightComponent.removeAll();
        var proxy = arg0List.getSelectedValue();
        if (proxy != null) {
            //inject
            var container = getContainer();
            container.registerObject("mcreator", () -> mcreator);
            container.registerTemporaryObject("index", () -> arg0List.getSelectedIndex());
            var argtype = proxy.getArg0Type();
            container.inject(argtype);
            container.endTemporaryLife();
            //inject
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", proxy.getArg0TypeName());

            var configurationPanel = new JPanel(new GridLayout(1, 2));
            var typeName = new VComboBox<String>();
            IArg0Type.arg0types.stream().map(a -> a.get().getName()).forEach(typeName::addItem);
            typeName.setOpaque(false);
            typeName.setSelectedItem(proxy.getArg0TypeName());
            typeName.addItemListener(a -> {
                if (a.getStateChange() == ItemEvent.SELECTED) {
                    var type = typeName.getSelectedItem();
                    jsonObject.addProperty("type", type);
                    reloadComponent(rightComponent);
                }
            });
            configurationPanel.add(L10N.label("elementgui.arg0.type"));
            configurationPanel.add(typeName);
            rightComponent.add("North", configurationPanel);

            rightComponent.add("Center", argtype.getEditor(proxy.getArg0Json(), jsonObject));
            proxy.setArg0Json(jsonObject);
        }
        SwingUtilities.invokeLater(() -> {
            rightComponent.repaint();
            rightComponent.revalidate();
        });
    }

    protected void openInEditingMode0(PluginProcedureModElement generatableElement) {
        this.inputsInline.setSelected(generatableElement.inputsInline);
        this.previousStatement.setText(generatableElement.previousStatement);
        this.nextStatement.setText(generatableElement.nextStatement);
        this.color.setColor(generatableElement.colour);
        this.builtInColor.setSelectedItem(Utils.nullToNoneOrNoneToNull(generatableElement.builtInColor, true));
        this.extensions.setTextList(generatableElement.extensions);
        if (!generatableElement.outputs.isEmpty()) {
            this.outputs.setListElements(generatableElement.outputs);
        }
        this.toolboxId.setSelectedItem(generatableElement.toolbox_id);
        this.group.setText(generatableElement.group);
        this.warnings.setTextList(generatableElement.warnings);
        this.requiredApis.setListElements(generatableElement.required_apis);
        model.addAll(generatableElement.arg0.stream().map(ArgTypeProxy::clone).toList());
        this.inputs.setTextList(generatableElement.inputs);
        this.fields.setTextList(generatableElement.fields);
        this.statements.setTextList(generatableElement.statements);
        this.toolboxInit.setTextList(generatableElement.toolbox_init);
        for (PluginProcedureModElement.Dependency dependency : generatableElement.dependencies) {
            try {
                this.dependencies.add((PluginProcedureModElement.Dependency) dependency.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        this.localizationValue.setText(generatableElement.localization);
        this.tooltip.setText(generatableElement.tooltip);
    }

    @Override public void reloadDataLists() {
        var stringArrayList = Utils.getAllCategories(mcreator, getBlocklyEditorType(), getBlocklyCategoryClass(),
                hasBuiltinCategories());
        ComboBoxUtil.updateComboBoxContents(toolboxId, stringArrayList.stream().sorted().toList());
    }

    @Override public void doSearch(Map.Entry<String, String> search) {

    }

    @Override public CompletableFuture<Void> refreshTable() {
        return CompletableFuture.runAsync(() -> {
            jTable.repaint();
            jTable.revalidate();

            arg0List.repaint();
            arg0List.revalidate();
        });
    }

    @Override public void showSearch(int index) {

    }

    public void setBuiltInColor(String builtInColor) {
        this.builtInColor.setSelectedItem(builtInColor);
    }

    private class DependenciesTableModule extends AbstractTableModel {

        @Override public int getRowCount() {
            return dependencies.size();
        }

        @Override public int getColumnCount() {
            return columns.length;
        }

        @Override public String getColumnName(int column) {
            return columns[column];
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            var row = dependencies.get(rowIndex);
            var columss = new String[] { row.getName(), row.getType() };
            return columss[columnIndex];
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            var row = dependencies.get(rowIndex);
            if (columns[columnIndex].equals("Dependency name")) {
                row.setName(aValue.toString());
            } else if (columns[columnIndex].equals("Type")) {
                row.setType(aValue.toString());
            }
        }
    }

    public JStringListField getWarnings() {
        return warnings;
    }

    public ArrayListListModel<ArgTypeProxy> getModel() {
        return model;
    }

    public JStringListField getInputs() {
        return inputs;
    }

    public JStringListField getFields() {
        return fields;
    }

    public JStringListField getStatements() {
        return statements;
    }
}
