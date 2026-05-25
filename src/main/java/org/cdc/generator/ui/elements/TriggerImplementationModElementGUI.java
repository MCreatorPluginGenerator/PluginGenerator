package org.cdc.generator.ui.elements;

import jdk.jfr.Description;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;
import org.cdc.generator.elements.TriggerImplementationModElement;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.factories.AutoCompletionFactory;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.Container;
import org.cdc.generator.utils.ioc.InjectField;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class TriggerImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<TriggerImplementationModElement> {

    final VComboBox<String> generator = new VComboBox<>();
    final VComboBox<String> triggerFileName = new VComboBox<>();
    private final JCheckBox enableCustom = createDefaultCheckBox();

    private final VTextField eventName = new VTextField();
    private final RSyntaxTextArea methodBody = new RSyntaxTextArea();

    public List<AbstractMap.SimpleEntry<String, String>> mappingEntries;

    @InjectField Container container;
    @InjectField Logger LOG;
    private JToolBar methodToolBar;

    public TriggerImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[] { "Name", "Map" });

        this.mappingEntries = new ArrayList<>();

        if (editingMode && isUnique()) {
            generator.setEnabled(false);
            triggerFileName.setEnabled(false);
        }
    }

    @Override public void initAfterAll() {
        initGUI();
        finalizeGUI();
        if (methodToolBar.getComponents().length == 0) {
            reloadToolBar();
        }

    }

    @Override protected void initGUI() {
        addGeneratorConfiguration(generator);

        triggerFileName.setEditable(true);
        triggerFileName.setValidator(new NotEmptyValidator(triggerFileName::getSelectedItem));
        addElementSelectorConfiguration("trigger_element_name", triggerFileName,
                () -> getTriggerModElement().getModElement());

        addConfigurationWithHelpEntry("enable_custom", enableCustom);

        eventName.setValidator(() -> {
            if (eventName.getText().contains("$")) {
                return new ValidationResult(ValidationResult.Type.ERROR, "Invalid char $");
            }
            if (eventName.getText() == null || eventName.getText().isEmpty()) {
                return new ValidationResult(ValidationResult.Type.ERROR, "Not empty");
            }
            return ValidationResult.PASSED;
        });

        addConfigurationWithHelpEntry("event_name", eventName);

        methodToolBar = new JToolBar();
        methodToolBar.add(syncLocalImplFile(methodBody::setText));

        var scrollpane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(methodBody, mcreator);
        AutoCompletionFactory.createDefaultParameterCompletion(methodBody, this::createCompletionProvider);
        var panel = PanelUtils.northAndCenterElement(methodToolBar, scrollpane);
        panel.setBorder(BorderFactory.createTitledBorder("Body (ctrl+1 to auto complete)"));

        generator.addItemListener(eventName -> reloadToolBar());
        addPage("Configuration", PanelUtils.northAndCenterElement(buildConfiguration(2), panel)).validate(generator)
                .validate(triggerFileName).validate(eventName).lazyValidate(
                        () -> methodBody.getText().contains("@Placeholder") ?
                                new AggregatedValidationResult.FAIL("You should replace the placeholder") :
                                new AggregatedValidationResult.PASS());

        initTable(new MappingTableModel());

        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        JButton remrow = createRemoveRowButton();

        remrow.addActionListener(a -> {
            jTable.editCellAt(-1, 0);
            var stack = new Stack<Integer>();
            Arrays.stream(jTable.getSelectedRows()).forEach(stack::add);
            while (!stack.empty()) {
                mappingEntries.remove((int) stack.pop());
            }
            jTable.setEditingRow(-1);
            jTable.setEditingColumn(-1);

            SwingUtilities.invokeLater(()->{
                jTable.revalidate();
                jTable.repaint();
            });
        });
        toolBar.add(remrow);

        addPage("Map", toolbarAndTable(toolBar));
    }

    private void reloadToolBar() {
        methodToolBar.removeAll();
        container.registerObject("modElementGui", () -> this);
        IExamplesProvider.examplesProviders.stream().forEach(a -> {
            if (a.type().isAnnotationPresent(Description.class)) {
                var des = a.type().getAnnotation(Description.class);
                if (des.value().equals("TriggerImplExamples")) {
                    container.inject(a.get())
                            .provideExamples(methodToolBar::add, text -> methodBody.setText(Objects.toString(text)),
                                    new String[] { generator.getSelectedItem() });
                }
            }
        });
        SwingUtilities.invokeLater(() -> {
            methodToolBar.repaint();
            methodToolBar.revalidate();
        });
    }

    @Override protected void openInEditingMode(TriggerImplementationModElement generatableElement) {
        this.generator.setSelectedItem(generatableElement.generatorName);
        this.triggerFileName.setSelectedItem(generatableElement.triggerFileName);
        this.enableCustom.setSelected(generatableElement.enableCustom);
        this.eventName.setText(generatableElement.eventName);
        this.methodBody.setText(generatableElement.methodBody);
        this.mappingEntries = Objects.requireNonNullElse(generatableElement.mappingEntries, new ArrayList<>());
    }

    @Override public TriggerImplementationModElement getElementFromGUI() {
        var element = new TriggerImplementationModElement(modElement);
        element.triggerFileName = triggerFileName.getSelectedItem();
        element.searchable = getTriggerModElement().getModElement().getName();
        element.generatorName = generator.getSelectedItem();
        element.enableCustom = enableCustom.isSelected();
        element.eventName = eventName.getText();
        element.methodBody = methodBody.getText();
        element.mappingEntries = new ArrayList<>(
                mappingEntries.stream().map(a -> new AbstractMap.SimpleEntry<>(a.getKey(), a.getValue())).toList());
        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return new URI(
                "https://mcreator.net/wiki/creating-global-triggers#:~:text=true%22%2C%0A%20%20%22has_result%22%3A%20%22true%22%0A%7D-,Make%20the%20code%20of%20your%20global%20trigger,-The%20folder");
    }

    public TriggerModElement getTriggerModElement() {
        for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
            if (modElement.getRegistryName().equals(triggerFileName.getSelectedItem())) {
                return (TriggerModElement) modElement.getGeneratableElement();
            }
        }
        LOG.error("Can not find trigger {}",triggerFileName.getSelectedItem());
        return null;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.TRIGGER.getRegistryName())) {
            stringArrayList.add(element.getRegistryName());
        }
        ComboBoxUtil.updateComboBoxContents(triggerFileName, stringArrayList);

        var map = getMappingEntries();
        for (TriggerModElement.Dependency dependency : getTriggerModElement().dependencies_provided) {
            if (!map.containsKey(dependency.getName()))
                mappingEntries.add(new AbstractMap.SimpleEntry<>(dependency.getName(), dependency.getType()));
        }
    }

    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.addCompletion(new BasicCompletion(provider, "dependencies"));
        provider.addCompletion(new BasicCompletion(provider, "${name}"));
        provider.addCompletion(new BasicCompletion(provider, "<#assign"));
        provider.addCompletion(new BasicCompletion(provider, "@procedureDependenciesCode"));
        provider.addCompletion(new BasicCompletion(provider, "execute()"));
        provider.addCompletion(new BasicCompletion(provider, "event"));

        Utils.initCompletionWithGenerator(provider, mcreator.getGenerator());
        return provider;
    }

    public Map<String, String> getMappingEntries() {
        var map = new HashMap<String, String>();
        for (AbstractMap.SimpleEntry<String, String> mappingEntry : mappingEntries) {
            map.put(mappingEntry.getKey(), mappingEntry.getValue());
        }
        return map;
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
            var columns = new String[] { row.getKey(), row.getValue() };
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
            var column = columns[columnIndex];
            var row = mappingEntries.get(rowIndex);
            if ("Map".equals(column)) {
                row.setValue(aValue.toString());
            }
        }
    }
}
