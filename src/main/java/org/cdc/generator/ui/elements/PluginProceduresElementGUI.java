package org.cdc.generator.ui.elements;

import com.google.gson.JsonObject;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.ArrayListListModel;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.services.types.ArgTypeProxy;
import org.cdc.generator.services.types.NoneArgType;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.interfaces.IArg0Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PluginProceduresElementGUI extends AbstractConfigurationTableModElementGUI<PluginProcedureModElement>
        implements ISearchable {

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
    protected final JStringListField inputs;
    protected final JStringListField fields;
    protected final JStringListField toolboxInit;

    private final ArrayListListModel<ArgTypeProxy> model;
    public JList<ArgTypeProxy> arg0List;

    public List<PluginProcedureModElement.Dependency> dependencies;

    public PluginProceduresElementGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[] { "Name", "Type" });
        this.inputsInline = createDefaultCheckBox();
        this.color = new JColor(mcreator, false, false);
        this.outputs = new JStringListField(mcreator, a -> Rules.getFileNameValidator(a::getText));
        this.warnings = new JStringListField(mcreator, null).setUniqueEntries(true);
        this.requiredApis = new JStringListField(mcreator, a -> Rules.getFileNameValidator(a::getText));
        this.model = new ArrayListListModel<>();
        this.arg0List = new JList<>(model);
        this.inputs = new JStringListField(mcreator, null);
        this.fields = new JStringListField(mcreator, null);
        this.toolboxInit = new JStringListField(mcreator, null);

        this.dependencies = new ArrayList<>();

        if (editingMode) {
            name.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(13, 2, 5, 5));

        name.setText(modElement.getRegistryName());
        name.setValidator(Rules.getFileNameValidator(name::getText));
        addNameConfiguration(name);
        addConfigurationWithHelpEntry("inputs_inline", inputsInline);
        addConfigurationWithHelpEntry("previous_statement", previousStatement);
        addConfigurationWithHelpEntry("next_statement", nextStatement);
        addConfigurationWithHelpEntry("color", color);
        addConfigurationWithHelpEntry("outputs", outputs);

        toolboxId.setEditable(true);
        addConfigurationWithHelpEntry("toolbox_id", toolboxId);
        addConfigurationWithHelpEntry("group", group);
        addConfigurationWithHelpEntry("warnings", warnings);
        addConfigurationWithHelpEntry("required_apis", requiredApis);
        addConfigurationWithHelpEntry("inputs", inputs);
        addConfigurationWithHelpEntry("fields", fields);
        addConfigurationWithHelpEntry("toolboxInit", toolboxInit);

        initTable(new DependenciesTableModule());

        JToolBar bar = new JToolBar();
        bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        bar.setFloatable(false);
        bar.setOpaque(false);

        JButton addrow = createAddButton();
        bar.add(addrow);
        JButton remrow = createRemoveRowButton();
        bar.add(remrow);

        addrow.addActionListener(a -> {
            dependencies.add(new PluginProcedureModElement.Dependency("name" + dependencies.size(), "type"));
            refreshTable();
        });
        remrow.addActionListener(a -> {
            jTable.editCellAt(-1, 0);
            Arrays.stream(jTable.getSelectedRows()).mapToObj(b -> dependencies.get(b)).forEach(c -> {
                dependencies.remove(c);
            });
            refreshTable();
        });

        addPage("Configuration", PanelUtils.northAndCenterElement(configurationPanel, toolbarAndTable(bar))).validate(
                name);

        JToolBar args0ToolBar = new JToolBar();

        JButton addLine = createAddButton();
        JButton removeLine = createRemoveRowButton();
        JSplitPane splitPane = new JSplitPane();
        arg0List.setBorder(BorderFactory.createTitledBorder("List"));
        arg0List.setOpaque(false);
        arg0List.setVisibleRowCount(20);
        arg0List.setMinimumSize(new Dimension(100, 200));
        splitPane.setLeftComponent(new JScrollPane(arg0List));
        JPanel rightComponent = new JPanel(new BorderLayout());
        rightComponent.setBorder(BorderFactory.createTitledBorder("Config"));
        splitPane.setRightComponent(rightComponent);
        args0ToolBar.add(addLine);
        addLine.addActionListener(a -> {
            var json = new JsonObject();
            json.addProperty("type", "input_dummy");
            model.add(new ArgTypeProxy(json));
        });
        removeLine.addActionListener(a -> model.remove(arg0List.getSelectedValue()));
        args0ToolBar.add(addLine);
        args0ToolBar.add(removeLine);
        arg0List.addListSelectionListener(listSelectionEvent -> {
            reloadComponent(rightComponent);
        });
        arg0List.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                label.setText(Objects.toString(value));
                return label;
            }
        });

        addPage("Args0", PanelUtils.northAndCenterElement(args0ToolBar, splitPane));
    }

    private void reloadComponent(JPanel rightComponent) {
        rightComponent.removeAll();
        var proxy = arg0List.getSelectedValue();
        if (proxy != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", proxy.getArg0Type().getName());
            var configurationPanel = new JPanel(new GridLayout(1, 2));
            var typeName = new VComboBox<String>();
            IArg0Type.arg0types.stream().map(a -> a.get().getName()).forEach(typeName::addItem);
            typeName.setOpaque(false);
            typeName.setSelectedItem(proxy.getArg0Type().getName());
            typeName.addItemListener(a -> {
                jsonObject.addProperty("type", typeName.getSelectedItem());
                if (proxy.getArg0Type().getType() == Arg0InputType.INPUT){
                    var newList = new ArrayList<>(inputs.getTextList());
                    newList.add(proxy.getUniqueName());
                    inputs.setTextList(newList);
                } else if (proxy.getArg0Type().getType() == Arg0InputType.FIELD){
                    var newList = new ArrayList<>(fields.getTextList());
                    newList.add(proxy.getUniqueName());
                    fields.setTextList(newList);
                }
                reloadComponent(rightComponent);
            });
            configurationPanel.add(L10N.label("elementgui.arg0.type"));
            configurationPanel.add(typeName);
            rightComponent.add("North", configurationPanel);
            rightComponent.add("Center", proxy.getArg0Type().getEditor(proxy.getArg0Json(), jsonObject));
            proxy.setArg0Json(jsonObject);
        }
        SwingUtilities.invokeLater(() -> {
            rightComponent.repaint();
            rightComponent.revalidate();
        });
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
        for (JsonObject jsonObject : generatableElement.arg0) {
            model.add(ArgTypeProxy.createArgTypeProxy(jsonObject));
        }
        this.inputs.setTextList(generatableElement.inputs);
        this.fields.setTextList(generatableElement.fields);
        this.toolboxInit.setTextList(generatableElement.toolbox_init);
        this.dependencies = generatableElement.dependencies;
    }

    @Override public PluginProcedureModElement getElementFromGUI() {
        this.modElement.setRegistryName(name.getText());
        var element = new PluginProcedureModElement(modElement);
        element.inputsInline = this.inputsInline.isSelected();
        element.previousStatement = this.previousStatement.getText();
        element.nextStatement = this.nextStatement.getText();
        element.colour = this.color.getColor();
        element.outputs = Objects.requireNonNullElse(outputs.getTextList(), List.of());
        element.toolbox_id = this.toolboxId.getSelectedItem();
        element.group = this.group.getText();
        element.warnings = Objects.requireNonNullElse(warnings.getTextList(), List.of());
        element.required_apis = Objects.requireNonNullElse(requiredApis.getTextList(), List.of());
        element.arg0 = model.stream().map(a -> a.getArg0Json().deepCopy()).toList();
        element.inputs = this.inputs.getTextList();
        element.fields = this.fields.getTextList();
        element.toolbox_init = this.toolboxInit.getTextList();
        element.dependencies = dependencies.stream().map(a -> {
            try {
                return (PluginProcedureModElement.Dependency) a.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        return element;
    }

    @Override @Nullable public URI contextURL() throws URISyntaxException {
        return null;
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
            if (columns[columnIndex].equals("Name")) {
                row.setName(aValue.toString());
            } else if (columns[columnIndex].equals("Type")) {
                row.setType(aValue.toString());
            }
        }
    }
}
