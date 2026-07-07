package org.cdc.generator.ui.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IUniqueElement;
import org.cdc.generator.ui.HelpTableHeader;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractConfigurationTableModElementGUI<E extends GeneratableElement> extends ModElementGUI<E> {

    protected final String[] columns;

    private JPanel configurationPanel;
    protected JTable jTable;

    protected String tableTitle = "Table";
    protected String configurationTitle = "Configuration";

    protected ArrayList<JComponent> componentList;

    private JButton removeRow;
    private JButton addRow;

    public AbstractConfigurationTableModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode, String[] columns) {
        super(mcreator, modElement, editingMode);
        this.columns = columns;
        this.componentList = new ArrayList<>();
    }

    /**
     * When you need @InjectField
     */
    public void initAfterAll() {

    }

    /**
     * init configurationPanel. You can call it optional.
     */
    @Deprecated protected void initConfiguration(LayoutManager layoutManager) {
        configurationPanel = new JPanel(layoutManager);
        configurationPanel.setOpaque(false);
        configurationPanel.setBorder(BorderFactory.createTitledBorder(configurationTitle));
    }

    /**
     * init table. You can call it optional.
     */
    protected void initTable(TableModel tableModel) {
        jTable = new JTable(tableModel);
        jTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && jTable.rowAtPoint(e.getPoint()) != jTable.getSelectedRow()) {
                    jTable.clearSelection();
                    jTable.editCellAt(-1, 0);
                }
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 3) {
                    if (addRow != null) {
                        addRow.doClick();
                    }
                }
            }
        });
        jTable.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (removeRow != null) {
                        removeRow.doClick();
                    }
                }
            }
        });
        jTable.setTableHeader(new HelpTableHeader(jTable.getColumnModel(), this,getHelpEntryAndLocalizationPrefix()));
        jTable.setFillsViewportHeight(true);
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable.setOpaque(false);
    }

    /**
     * a builtin configuration.
     */
    protected void addGeneratorConfiguration(VComboBox<String> generator) {
        generator.setValidator(new NotEmptyValidator(generator::getSelectedItem));
        var generators = new ArrayList<>(Utils.getAllSupportedGenerators());
        ComboBoxUtil.updateComboBoxContents(generator, generators);
        generator.setSelectedItem(PluginMakerPreference.INSTANCE.preferGenerator.get());
        generator.setEditable(true);
        generator.setPreferredSize(Utils.tryToGetTextFieldSize());
        generator.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                var generatorName = value.toString();
                try {
                    //other than addon...
                    var flavor = generatorName.split("-")[0];
                    if ("addon".equals(flavor)) {
                        flavor = "bedrock";
                    }
                    label.setIcon(UIRES.get("16px." + flavor));
                } catch (Exception ignored) {

                }
                return label;
            }
        });
        componentList.add(
                HelpUtils.wrapWithHelpButton(this.withEntry(getHelpEntryAndLocalizationPrefix() + "/generator"),
                        L10N.label("elementgui.common.generator")));
        componentList.add(generator);
    }

    protected void addNameConfiguration(JComponent component) {
        component.setOpaque(false);
        component.setPreferredSize(Utils.tryToGetTextFieldSize());
        componentList.add(HelpUtils.wrapWithHelpButton(this.withEntry(getHelpEntryAndLocalizationPrefix() + "/name"),
                L10N.label("elementgui.common.name")));
        componentList.add(component);
    }

    protected void addConfigurationWithHelpEntry(String name, JComponent component) {
        component.setOpaque(false);
        var label = L10N.label("elementgui." + getHelpEntryAndLocalizationPrefix() + "." + name);
        label.setText(label.getText() + ": ");
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    var content = new StringSelection(label.getText());
                    label.getToolkit().getSystemClipboard().setContents(content, content);
                }
            }
        });
        componentList.add(
                HelpUtils.wrapWithHelpButton(this.withEntry(getHelpEntryAndLocalizationPrefix() + "/" + name), label));
        componentList.add(component);
    }

    protected JPanel buildConfiguration(int columns) {
        configurationPanel = new JPanel(new GridLayout(componentList.size() / columns, columns, 5, 5));
        configurationPanel.setOpaque(false);
        configurationPanel.setBorder(BorderFactory.createTitledBorder(configurationTitle));
        for (JComponent component : componentList) {
            configurationPanel.add(component);
        }
        if (this instanceof IQuickCreateImplModElement iHasImplModElement) {
            Utils.registerCreateImplShortCut(iHasImplModElement, configurationPanel);
        }

        return configurationPanel;
    }

    protected void addElementSelectorConfiguration(String name, JComponent component,
            Supplier<ModElement> elementNameSupplier) {
        var edit = new JButton(UIRES.get("16px.edit"));
        edit.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                var el = elementNameSupplier.get();
                if (el != null) {
                    edit.setToolTipText("Edit the element " + el.getName());
                }
            }
        });

        edit.addActionListener(a -> {
            var element = elementNameSupplier.get();
            if (element != null) {
                element.getType().getModElementGUI(mcreator, element, true).showView();
            } else {
                JOptionPane.showMessageDialog(this, "Can not open " + elementNameSupplier.get());
            }
        });
        addConfigurationWithHelpEntry(name, PanelUtils.centerAndEastElement(component, edit));
    }

    protected JComponent toolbarAndTable(JComponent north) {
        JPanel panel = PanelUtils.northAndCenterElement(north, wrapTable());
        panel.setBorder(BorderFactory.createTitledBorder(tableTitle));
        return panel;
    }

    protected JComponent wrapTable() {
        return new JScrollPane(jTable);
    }

    /**
     * This will also register the key listener to remove line.
     */
    protected JButton createJTableRemoveRowButton() {
        removeRow = createRemoveRowButton();
        return removeRow;
    }

    protected JButton createRemoveRowButton() {
        JButton remrow = new JButton(UIRES.get("16px.delete"));
        remrow.setContentAreaFilled(false);
        remrow.setOpaque(false);
        ComponentUtils.deriveFont(remrow, 11);
        remrow.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 1));
        remrow.setToolTipText("Remove (Shortcut: Delete)");
        return remrow;
    }

    /**
     * This will also register the key listener to remove line.
     */
    protected JButton createJTableAddButton() {
        addRow = createAddButton();
        return addRow;
    }

    protected JButton createAddButton() {
        JButton addrow = new JButton(UIRES.get("16px.add"));
        addrow.setContentAreaFilled(false);
        addrow.setOpaque(false);
        ComponentUtils.deriveFont(addrow, 11);
        addrow.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 2));
        addrow.setToolTipText("Add");
        return addrow;
    }

    protected JCheckBox createDefaultCheckBox() {
        return L10N.checkbox("elementgui.common.enable");
    }

    private Boolean cachedBoolean;

    protected boolean isUnique() {
        if (cachedBoolean != null) {
            return cachedBoolean;
        }
        if (modElement.getGeneratableElement() instanceof IUniqueElement unique) {
            return mcreator.getWorkspaceInfo().getGElementsOfType(modElement.getTypeString()).stream().noneMatch(a -> {
                if (unique != a && a instanceof IUniqueElement unique1) {
                    return cachedBoolean = unique1.getUniqueID().equals(unique.getUniqueID());
                }
                return cachedBoolean = false;
            });
        }
        return cachedBoolean = true;
    }

    protected String getHelpEntryAndLocalizationPrefix() {
        return modElement.getTypeString();
    }

    protected JButton syncLocalImplFile(Consumer<String> consumer) {
        var sync = new JButton("S");
        sync.setToolTipText("Sync from local file");
        sync.addActionListener(a -> {
            var files = getModElement().getAssociatedFiles();
            if (!files.isEmpty()) {
                var str = FileIO.readFileToString(files.getFirst());
                consumer.accept(str);
            }
        });
        return sync;
    }
}
