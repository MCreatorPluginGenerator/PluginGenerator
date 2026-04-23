package org.cdc.generator.ui.elements;

import net.mcreator.element.GeneratableElement;
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
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class AbstractConfigurationTableModElementGUI<E extends GeneratableElement> extends ModElementGUI<E> {

    protected final String[] columns;

    protected JPanel configurationPanel;
    protected JTable jTable;

    public AbstractConfigurationTableModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode, String[] columns) {
        super(mcreator, modElement, editingMode);
        this.columns = columns;
    }

    /**
     * When you need @InjectField
     */
    public void initAfterAll() {

    }

    /**
     * init configurationPanel. You can call it optional.
     */
    protected void initConfiguration(LayoutManager layoutManager) {
        configurationPanel = new JPanel(layoutManager);
        configurationPanel.setOpaque(false);
        configurationPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
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
            }
        });
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
        configurationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry(modElement.getTypeString() + "/generator"),
                L10N.label("elementgui.common.generator")));
        configurationPanel.add(generator);
    }

    protected void addNameConfiguration(JComponent component) {
        component.setOpaque(false);
        component.setPreferredSize(Utils.tryToGetTextFieldSize());
        configurationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry(modElement.getTypeString() + "/name"),
                L10N.label("elementgui.common.name")));
        configurationPanel.add(component);
    }

    protected void addConfigurationWithHelpEntry(String name, JComponent component) {
        component.setOpaque(false);
        configurationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry(modElement.getTypeString() + "/" + name),
                L10N.label("elementgui." + modElement.getTypeString() + "." + name)));
        configurationPanel.add(component);
    }

    protected void addElementSelectorConfiguration(String name, JComponent component,
            Supplier<String> elementNameSupplier) {
        var edit = new JButton(UIRES.get("16px.edit"));
        edit.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                edit.setToolTipText("Edit the element " + elementNameSupplier.get());
            }
        });

        edit.addActionListener(a -> {
            var element = mcreator.getWorkspace().getModElementByName(elementNameSupplier.get());
            if (element != null) {
                element.getType().getModElementGUI(mcreator, element, true).showView();
            } else {
                JOptionPane.showMessageDialog(this, "Can not open " + elementNameSupplier.get());
            }
        });
        addConfigurationWithHelpEntry(name, PanelUtils.centerAndEastElement(component, edit));
    }

    protected JComponent toolbarAndTable(JComponent north) {
        JPanel panel = PanelUtils.northAndCenterElement(north, new JScrollPane(jTable));
        panel.setBorder(BorderFactory.createTitledBorder("Table"));
        return panel;
    }

    protected JComponent wrapTable() {
        return new JScrollPane(jTable);
    }

    protected JButton createRemoveRowButton() {
        JButton remrow = new JButton(UIRES.get("16px.delete"));
        remrow.setContentAreaFilled(false);
        remrow.setOpaque(false);
        ComponentUtils.deriveFont(remrow, 11);
        remrow.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 1));
        remrow.setToolTipText("Remove");
        return remrow;
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

    protected boolean isUnique() {
        if (modElement.getGeneratableElement() instanceof IUniqueElement unique) {
            return mcreator.getWorkspaceInfo().getGElementsOfType(modElement.getTypeString()).stream().noneMatch(a -> {
                if (unique != a && a instanceof IUniqueElement unique1) {
                    System.out.println(unique1.getUniqueID());
                    return unique1.getUniqueID().equals(unique.getUniqueID());
                }
                return false;
            });
        }
        return true;
    }
}
