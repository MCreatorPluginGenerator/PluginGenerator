package org.cdc.generator.ui.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        for (String supportedGenerator : Utils.getAllSupportedGenerators()) {
            generator.addItem(supportedGenerator);
        }
        generator.setSelectedItem(PluginMakerPreference.INSTANCE.preferGenerator.get());
        generator.setEditable(true);
        generator.setPreferredSize(Utils.tryToGetTextFieldSize());
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
}
