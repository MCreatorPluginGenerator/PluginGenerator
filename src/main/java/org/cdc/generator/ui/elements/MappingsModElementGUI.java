package org.cdc.generator.ui.elements;

import net.mcreator.generator.Generator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.DataListModElement;
import org.cdc.generator.elements.MappingsModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.*;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.validators.NotEmptyValidator;
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

public class MappingsModElementGUI extends AbstractConfigurationTableModElementGUI<MappingsModElement>
        implements ISearchable {

    private final VComboBox<String> generator = new VComboBox<>();
    private final VComboBox<String> datalistName = new VComboBox<>();

    public List<MappingsModElement.MappingEntry> mappingEntries;

    // the 0 is the last search index
    private final ArrayList<Integer> lastSearchResult;

    public MappingsModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[] { "Name", "Mapping" });

        this.mappingEntries = new ArrayList<>();
        this.lastSearchResult = new ArrayList<>(List.of(0));

        if (editingMode) {
            generator.setEnabled(false);
            datalistName.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(2, 2));

        addGeneratorConfiguration(generator);

        datalistName.setEditable(false);
        datalistName.setValidator(new NotEmptyValidator(datalistName::getSelectedItem));
        addConfigurationWithHelpEntry("datalist_name", datalistName);

        JToolBar bar = new JToolBar();
        bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        bar.setFloatable(false);
        bar.setOpaque(false);
        bar.setOpaque(false);

        var syncDatalist = new JButton(UIRES.get("impfile"));
        syncDatalist.setToolTipText("Import from element and memory");
        syncDatalist.setOpaque(false);

        initTable(new MappingTableModel());

        jTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int rowIndex, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus,
                        rowIndex, column);
                if (value != null)
                    label.setToolTipText(value + ", index=" + rowIndex);
                return label;
            }
        });
        jTable.setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value1, boolean isSelected, int rowIndex,
                    int column) {
                var row = mappingEntries.get(rowIndex);
                if (columns[column].equals("Mapping")) {
                    JToolBar placeholder = new JToolBar();
                    placeholder.setFloatable(false);
                    placeholder.setLayout(new FlowLayout());
                    placeholder.setBorder(BorderFactory.createTitledBorder("Placeholders"));
                    RSyntaxTextArea jTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();

                    Stream.of(Constants.mappingPlaceholders).forEach(a -> {
                        JButton appendName = new JButton(a);
                        appendName.setContentAreaFilled(false);
                        appendName.setOpaque(false);
                        appendName.setHorizontalTextPosition(SwingConstants.LEFT);
                        appendName.addActionListener(event -> {
                            jTextArea.insert(a, jTextArea.getCaretPosition());
                        });
                        placeholder.add(appendName);
                    });
                    int op = DialogUtils.showOptionPaneWithTextAreaAndToolBar(jTextArea, placeholder, mcreator,
                            "Edit Mapping (one line one item)", row.getMappingContent());
                    if (op == JOptionPane.YES_OPTION) {
                        String str = jTextArea.getText();
                        BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
                        try {
                            var list = row.getMappingContent();
                            list.clear();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                list.add(line);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        row.setEdited(MappingsModElement.MappingEntry.isEdited(generator.getSelectedItem(),
                                ElementsUtils.getDataListName(mcreator.getWorkspace(), datalistName.getSelectedItem()),
                                row));
                    }
                    return null;
                }
                return super.getTableCellEditorComponent(jTable, value1, isSelected, rowIndex, column);
            }
        });
        syncDatalist.addActionListener(e -> {
            var datalist = mcreator.getWorkspace().getModElementByName(datalistName.getSelectedItem());
            //
            MappingsModElementGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //
            if (datalist != null) {
                String dataListName = datalist.getRegistryName();
                var memory = Generator.GENERATOR_CACHE.get(generator.getSelectedItem()).getMappingLoader()
                        .getMapping(dataListName);
                var cacheSet = new HashSet<MappingsModElement.MappingEntry>();
                var set = new HashSet<String>();
                mappingEntries.forEach(a -> {
                    if (a.isEdited()) {
                        cacheSet.add(a);
                        set.add(a.getName());
                    }
                });
                mappingEntries.clear();
                if (memory != null) {
                    for (Map.Entry<?, ?> entry : memory.entrySet()) {
                        var key = entry.getKey().toString();
                        // exclude
                        if (!set.contains(key)) {
                            set.add(key);
                            mappingEntries.add(new MappingsModElement.MappingEntry(key,
                                    Utils.convertYamlToList(entry.getValue())));
                        }
                    }
                }
                // add custom
                for (DataListModElement.DataListEntry entry : ((DataListModElement) Objects.requireNonNull(
                        datalist.getGeneratableElement())).entries) {
                    if (set.isEmpty() || !set.contains(entry.getName()))
                        mappingEntries.add(new MappingsModElement.MappingEntry(entry.getName(), new ArrayList<>()));
                }
                mappingEntries.addAll(cacheSet);
                JOptionPane.showMessageDialog(mcreator,
                        "Total: " + mappingEntries.size() + ", Edited: " + cacheSet.size());
            }

            MappingsModElementGUI.this.setCursor(Cursor.getDefaultCursor());
            refreshTable();
        });

        bar.add(syncDatalist);
        bar.add(initSearchBar(lastSearchResult));

        addPage("edit", PanelUtils.northAndCenterElement(configurationPanel, toolbarAndTable(bar))).validate(generator)
                .validate(datalistName);
    }

    @Override protected void openInEditingMode(MappingsModElement generatableElement) {
        datalistName.setSelectedItem(generatableElement.datalistElementName);
        generator.setSelectedItem(generatableElement.generatorName);
        mappingEntries = new ArrayList<>(generatableElement.mappingsContent);
    }

    @Override public MappingsModElement getElementFromGUI() {
        var element = new MappingsModElement(modElement);
        element.datalistElementName = datalistName.getSelectedItem();
        element.generatorName = generator.getSelectedItem();
        element.mappingsContent = mappingEntries.stream().map(MappingsModElement.MappingEntry::clone).toList();
        modElement.setRegistryName(element.getDatalistName());
        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspace().getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.DATA_LIST.getRegistryName())) {
            stringArrayList.add(element.getName());
        }
        ComboBoxUtil.updateComboBoxContents(datalistName, stringArrayList);
    }

    public void doSearch(Map.Entry<String, String> search) {
        lastSearchResult.clear();
        // cache
        lastSearchResult.add(0);
        for (int i = 0; i < mappingEntries.size(); i++) {
            var entry = mappingEntries.get(i);
            AtomicInteger index = new AtomicInteger();
            if (Stream.of(entry.getName(), entry.getMappingContent().toString())
                    .map(a -> Map.entry(columns[index.getAndIncrement()], Rules.SearchRules.applyIgnoreCaseRule(a)))
                    .anyMatch(a -> {
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

    private class MappingTableModel extends AbstractTableModel {
        @Override public int getRowCount() {
            return mappingEntries.size();
        }

        @Override public int getColumnCount() {
            return columns.length;
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            var row = mappingEntries.get(rowIndex);
            var columns = new String[] { row.getName(), row.getMappingContent().toString() };
            return columns[columnIndex];
        }

        @Override public String getColumnName(int column) {
            return columns[column];
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }
}
