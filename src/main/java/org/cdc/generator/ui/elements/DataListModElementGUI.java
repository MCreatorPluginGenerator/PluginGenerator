package org.cdc.generator.ui.elements;

import com.google.common.io.Files;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.DataListModElement;
import org.cdc.generator.ui.ResourcePanelIcons;
import org.cdc.generator.utils.DialogUtils;
import org.cdc.generator.utils.Rules;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.ZipUtils;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.validators.DuplicatedElementValidator;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class DataListModElementGUI extends AbstractConfigurationTableModElementGUI<DataListModElement>
        implements ISearchable {

    private final VComboBox<String> datalistName = new VComboBox<>();
    private final JCheckBox generateDataList = createDefaultCheckBox();
    private final VTextField dialogMessage = new VTextField();

    public List<DataListModElement.DataListEntry> entries;

    // the 0 is the last search index
    private final ArrayList<Integer> lastSearchResult;
    private ResourcePanelIcons resourcePanelIcons;
    private HashSet<String> types;

    public DataListModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode,
                new String[] { "Name", "Readable name", "Type", "Texture", "Description", "Others" });

        this.entries = new ArrayList<>();
        this.lastSearchResult = new ArrayList<>(List.of(0));

        if (editingMode) {
            datalistName.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(3, 2));

        datalistName.setEditable(true);
        datalistName.setValidator(Rules.getFileNameValidator(datalistName::getSelectedItem));
        datalistName.setSelectedItem(modElement.getRegistryName());
        var list = DataListLoader.getCache().keySet().stream().sorted().toList();
        ComboBoxUtil.updateComboBoxContents(datalistName, list);
        addNameConfiguration(datalistName);

        generateDataList.setSelected(true);
        generateDataList.setOpaque(false);
        addConfigurationWithHelpEntry("generate_datalists", generateDataList);

        dialogMessage.setOpaque(false);
        addConfigurationWithHelpEntry("dialog_message", dialogMessage);

        initTable(new DataListTableModel());
        jTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int rowIndex, int column) {
                var row = entries.get(rowIndex);
                JLabel label = (JLabel) super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus,
                        rowIndex, column);
                var attributes = new ArrayList<>();
                attributes.add("Builtin=" + row.isBuiltIn());
                attributes.add("Index=" + rowIndex);
                label.setToolTipText(value + attributes.toString());
                return label;
            }
        });
        var comboBox = new VComboBox<>();
        comboBox.setEditable(true);
        types = new HashSet<>();
        jTable.setDefaultEditor(String.class, new DefaultCellEditor(comboBox) {

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value1, boolean isSelected, int rowIndex,
                    int column) {
                comboBox.removeAllItems();
                var row = entries.get(rowIndex);
                if (columns[column].equals("Others")) {
                    RSyntaxTextArea jTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
                    JToolBar toolBar = new JToolBar();
                    toolBar.setBorder(BorderFactory.createTitledBorder("Toolbar"));
                    JButton example = new JButton("example");
                    example.addActionListener(actionEvent -> {
                        jTextArea.setText("registry_name=null");
                    });
                    toolBar.add(example);

                    int op = DialogUtils.showOptionPaneWithTextAreaAndToolBar(jTextArea, toolBar, mcreator,
                            "Edit others (Format: properties)", row.getOthers());
                    if (op == JOptionPane.YES_OPTION) {
                        String str = jTextArea.getText();
                        var prop = new Properties();
                        try {
                            prop.load(new StringReader(str));
                            var cacheMap = new HashMap<String, String>();
                            prop.forEach((key, value) -> cacheMap.put(key.toString(), value.toString()));
                            row.setOthers(cacheMap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                } else if ("Texture".equals(columns[column])) {
                    for (File element : resourcePanelIcons.getAllElements()) {
                        comboBox.addItem(Files.getNameWithoutExtension(element.getName()));
                    }
                    var file = Utils.tryToFindCorePlugin();
                    if (file.isDirectory()) {
                        var file1 = new File(file, "datalists/icons");
                        for (File listFile : Objects.requireNonNullElse(file1.listFiles(), new File[0])) {
                            comboBox.addItem(Files.getNameWithoutExtension(listFile.getName()));
                        }
                    } else {
                        for (String s : ZipUtils.tryToGetTexturesFromZip(file)) {
                            comboBox.addItem(s);
                        }
                    }
                } else if ("Type".equals(columns[column])) {
                    for (String type : types) {
                        comboBox.addItem(type);
                    }
                }
                return super.getTableCellEditorComponent(jTable, value1, isSelected, rowIndex, column);
            }
        });
        JToolBar bar = new JToolBar();
        bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
        bar.setFloatable(false);
        bar.setOpaque(false);

        JButton addrow = createAddButton();
        bar.add(addrow);
        JButton remrow = createRemoveRowButton();
        bar.add(remrow);
        bar.add(Utils.initSearchComponent(lastSearchResult, this));

        addrow.addActionListener(e -> {
            entries.add(entries.isEmpty() ?
                    new DataListModElement.DataListEntry("name") :
                    DataListModElement.DataListEntry.copyCommonValueOf(entries.getLast()));
            if (!isEditingMode()) {
                JOptionPane.showMessageDialog(mcreator, "If you edit datalist name, you will lose your work", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
            refreshTable().thenAcceptAsync(a -> {
                jTable.changeSelection(entries.size() - 1, 0, false, false);
            });
        });
        remrow.addActionListener(e -> {
            jTable.editCellAt(-1, 0);

            var stack = new Stack<Integer>();
            Arrays.stream(jTable.getSelectedRows()).forEach(stack::add);
            while (!stack.empty()) {
                entries.remove((int) stack.pop());
            }
            refreshTable();
        });
        datalistName.addItemListener(e -> {
            reloadDataLists();
            refreshTable();
        });

        addPage("Configuration",
                PanelUtils.northAndCenterElement(configurationPanel, toolbarAndTable(bar))).lazyValidate(
                new DuplicatedElementValidator(
                        () -> entries.stream().map(DataListModElement.DataListEntry::getName).toList(),
                        b -> jTable.changeSelection(b, 0, false, false))).validate(datalistName);

        resourcePanelIcons = new ResourcePanelIcons((WorkspacePanel) mcreator.getWorkspacePanel(), this);
        resourcePanelIcons.reloadElements();
        addPage("Icons", resourcePanelIcons);
    }

    @Override public void doSearch(Map.Entry<String, String> search) {
        lastSearchResult.clear();
        // cache
        lastSearchResult.add(0);
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            AtomicInteger atomicInteger = new AtomicInteger();
            if (Stream.of(entry.getName(), entry.getReadableName(), entry.getType(), entry.getTexture(),
                            entry.getDescription(), entry.getOther().toString())
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

    @Override protected void openInEditingMode(DataListModElement generatableElement) {
        this.entries = new ArrayList<>(generatableElement.entries);
        this.generateDataList.setSelected(generatableElement.generateDataList);
        this.dialogMessage.setText(generatableElement.dialogMessage);
        for (DataListModElement.DataListEntry entry : entries) {
            types.add(entry.getType());
        }
    }

    @Override public DataListModElement getElementFromGUI() {
        modElement.setRegistryName(datalistName.getSelectedItem());
        DataListModElement dataListModElement = new DataListModElement(modElement);
        dataListModElement.generateDataList = generateDataList.isSelected();
        dataListModElement.entries = entries.stream().map(DataListModElement.DataListEntry::clone).toList();
        dataListModElement.dialogMessage = dialogMessage.getText();
        return dataListModElement;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public void reloadDataLists() {
        if (DataListLoader.getCache().containsKey(datalistName.getSelectedItem()) && !isEditingMode()) {
            entries.clear();
            for (DataListEntry dataListEntry : DataListLoader.loadDataList(datalistName.getSelectedItem())) {
                var dataListEntry1 = DataListModElement.DataListEntry.copyValueOf(dataListEntry);
                dataListEntry1.setBuiltIn(true);
                entries.add(dataListEntry1);
            }
        }
    }

    public HashSet<String> getTypes() {
        return types;
    }

    @Override public CompletableFuture<Void> refreshTable() {
        return CompletableFuture.runAsync(() -> {
            jTable.repaint();
            jTable.revalidate();
        });
    }

    @Override public void showSearch(int index) {
        jTable.changeSelection(index, 0, false, false);
    }

    private class DataListTableModel extends AbstractTableModel {

        @Override public int getRowCount() {
            return entries.size();
        }

        @Override public int getColumnCount() {
            return columns.length;
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            var row = entries.get(rowIndex);
            var columns = new String[] { row.getName(), row.getReadableName(), row.getType(), row.getTexture(),
                    row.getDescription(), row.getOther().toString() };
            return columns[columnIndex];
        }

        @Override public String getColumnName(int column) {
            return columns[column];
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            var row = entries.get(rowIndex);
            return columnIndex == 0 || row.getName().charAt(0) != '_';
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            var row = entries.get(rowIndex);
            if (aValue == null || row.isBuiltIn()) {
                return;
            }
            switch (getColumnName(columnIndex)) {
            case "Name" -> {
                if (Rules.DATALIST_ENTRY_NAME.matcher(aValue.toString()).matches()) {
                    row.setName(aValue.toString());
                } else {
                    JOptionPane.showMessageDialog(mcreator, "It doesn't match the rule!", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            case "Readable name" -> row.setReadableName(aValue.toString());
            case "Type" -> row.setType(aValue.toString());
            case "Texture" -> row.setTexture(aValue.toString());
            case "Description" -> row.setDescription(aValue.toString());
            }
        }
    }
}
