package org.cdc.generator.ui.elements;

import jdk.jfr.Description;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.APIModElement;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.DialogUtils;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.Inject;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class APIModElementGUI extends AbstractConfigurationTableModElementGUI<APIModElement> implements ISearchable {

    private final VTextField name = new VTextField();
    private final VTextField displayName = new VTextField();

    public List<APIModElement.Configuration> configurations = new ArrayList<>();
    private final ArrayList<Integer> lastSearchResult = new ArrayList<>(List.of(0));

    @Inject private PluginMakerPreference preferences;

    public APIModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode,
                new String[] { "Generator", "Required when enable", "Update files", "Version range", "Gradle" });

        if (editingMode) {
            name.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(2, 2, 5, 5));

        name.setValidator(Rules.getFileNameValidator(name::getText));
        name.setText(modElement.getRegistryName());
        addNameConfiguration(name);

        displayName.setPreferredSize(Utils.tryToGetTextFieldSize());
        displayName.setText(modElement.getName());
        addConfigurationWithHelpEntry("display_name", displayName);

        initTable(new APIModElementGUITableRenderer());

        JToolBar bar = new JToolBar();
        bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        bar.setFloatable(false);
        bar.setOpaque(false);

        JButton addrow = new JButton(UIRES.get("16px.add"));
        addrow.setContentAreaFilled(false);
        addrow.setOpaque(false);
        ComponentUtils.deriveFont(addrow, 11);
        addrow.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 2));
        bar.add(addrow);

        JButton remrow = new JButton(UIRES.get("16px.delete"));
        remrow.setContentAreaFilled(false);
        remrow.setOpaque(false);
        ComponentUtils.deriveFont(remrow, 11);
        remrow.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 1));
        bar.add(remrow);
        bar.add(Utils.initSearchComponent(lastSearchResult, this));

        VComboBox<String> generatorCom = new VComboBox<>();
        generatorCom.setEditable(true);

        jTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                if (columns[column].equals("Gradle")) {
                    label.setText(Arrays.toString(label.getText().split(System.lineSeparator())));
                }
                return label;
            }
        });
        jTable.setDefaultEditor(String.class, new DefaultCellEditor(generatorCom) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
                    int columnIndex) {
                var row = configurations.get(rowIndex);
                var column1 = columns[columnIndex];
                if (column1.equals("Gradle")) {
                    JToolBar toolBar = new JToolBar();
                    toolBar.setOpaque(false);
                    toolBar.setBorder(BorderFactory.createTitledBorder("Gradles"));
                    RSyntaxTextArea jTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
                    IExamplesProvider.examplesProviders.stream().forEach(a -> {
                        if (a.type().isAnnotationPresent(Description.class)) {
                            var desc = a.type().getAnnotation(Description.class);
                            if ("Gradles".equals(desc.value())) {
                                a.get().provideExamples(toolBar::add, text -> jTextArea.setText(Objects.toString(text)),
                                        new String[0]);
                            }
                        }
                    });
                    int op = DialogUtils.showOptionPaneWithTextAreaAndToolBar(jTextArea, toolBar, mcreator,
                            "Edit Gradle (one line one item)", row.getYamlGradle());
                    if (op == JOptionPane.YES_OPTION) {
                        String str = jTextArea.getText();
                        row.setGradle(str);
                    }
                    return null;
                }

                generatorCom.removeAllItems();
                if (column1.equals("Generator")) {
                    var selected = generatorCom.getSelectedItem();
                    for (String allSupportedGenerator : Utils.getAllSupportedGenerators()) {
                        generatorCom.addItem(allSupportedGenerator);
                    }
                    generatorCom.setSelectedItem(selected);
                } else if ("Version range".equals(column1)) {
                    var selected = generatorCom.getSelectedItem();
                    generatorCom.addItem("[0,)");
                    generatorCom.setSelectedItem(selected);
                }
                return super.getTableCellEditorComponent(table, value, isSelected, rowIndex, columnIndex);
            }
        });
        jTable.setDefaultEditor(List.class, new DefaultCellEditor(new VComboBox<>()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value1, boolean isSelected, int rowIndex,
                    int column) {
                // Update files
                var row = configurations.get(rowIndex);
                var jTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
                int op = DialogUtils.showOptionPaneWithTextArea(jTextArea, mcreator,
                        "Edit Update files (one line one item)", row.getUpdateFiles());
                if (op == JOptionPane.YES_OPTION) {
                    String str = jTextArea.getText();
                    BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
                    try {
                        var list = row.getUpdateFiles();
                        list.clear();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            list.add(line);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null;
            }

        });
        addrow.addActionListener(e -> {
            var config = new APIModElement.Configuration();
            config.setGenerator(preferences.preferGenerator.get());
            configurations.add(config);
            refreshTable();
        });
        remrow.addActionListener(e -> {
            jTable.editCellAt(-1, 0);
            Arrays.stream(jTable.getSelectedRows()).mapToObj(b -> configurations.get(b)).forEach(c -> {
                configurations.remove(c);
            });
            refreshTable();
        });

        addPage("edit", PanelUtils.northAndCenterElement(configurationPanel, toolbarAndTable(bar))).validate(name);
    }

    @Override protected void openInEditingMode(APIModElement generatableElement) {
        this.name.setText(modElement.getRegistryName());
        this.displayName.setText(generatableElement.apiName);
        this.configurations = generatableElement.configurations;
    }

    @Override public APIModElement getElementFromGUI() {
        modElement.setRegistryName(name.getText());
        APIModElement apiModElement = new APIModElement(modElement);
        apiModElement.apiName = displayName.getText();
        apiModElement.configurations = configurations.stream().map(APIModElement.Configuration::clone).toList();
        return apiModElement;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public void doSearch(Map.Entry<String, String> search) {
        lastSearchResult.clear();
        // cache
        lastSearchResult.add(0);
        for (int i = 0; i < configurations.size(); i++) {
            var entry = configurations.get(i);
            AtomicInteger atomicInteger = new AtomicInteger();
            if (Stream.of(entry.getGenerator(), entry.isRequiredWhenEnable() + "", entry.getUpdateFiles().toString(),
                            entry.getVersionRange(), entry.getGradle())
                    .map(a -> Map.entry(columns[atomicInteger.getAndIncrement()],
                            Rules.SearchRules.applyIgnoreCaseRule(a))).anyMatch(a -> {
                        if (!search.getKey().isBlank()) {
                            if (a.getKey().equalsIgnoreCase(search.getKey())) {
                                return a.getValue().contains(search.getValue());
                            }
                            return false;
                        }
                        return a.getValue().contains(search.getValue());
                    })) {
                lastSearchResult.add(i);
            }
        }
    }

    @Override public CompletableFuture<Void> refreshTable() {
        return CompletableFuture.runAsync(()->{
            jTable.repaint();
            jTable.revalidate();
        });
    }

    @Override public void showSearch(int index) {
        jTable.changeSelection(index, 0, false, false);
    }

    private class APIModElementGUITableRenderer extends AbstractTableModel {

        @Override public int getRowCount() {
            return configurations.size();
        }

        @Override public int getColumnCount() {
            return columns.length;
        }

        @Override public String getColumnName(int column) {
            return columns[column];
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            var row = configurations.get(rowIndex);
            Object[] values = new Object[] { row.getGenerator(), row.isRequiredWhenEnable(), row.getUpdateFiles(),
                    row.getVersionRange(), row.getGradle() };
            return values[columnIndex];
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1)
                return Boolean.class;
            if (columnIndex == 2)
                return List.class;
            return String.class;
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            var row = configurations.get(rowIndex);
            if (aValue == null) {
                return;
            }
            switch (getColumnName(columnIndex)) {
            case "Generator" -> row.setGenerator(aValue.toString());
            case "Required when enable" -> row.setRequiredWhenEnable((boolean) aValue);
            case "Version range" -> row.setVersionRange(aValue.toString());
            }
        }
    }
}
