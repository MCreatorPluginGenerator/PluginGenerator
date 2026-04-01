package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.TriggerImplementationModElement;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.YamlUtils;
import org.cdc.generator.utils.factories.AutoCompletionFactory;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class TriggerImplementationModElementGUI
        extends AbstractConfigurationTableModElementGUI<TriggerImplementationModElement> {

    private final VComboBox<String> generator = new VComboBox<>();
    private final VComboBox<String> triggerElementName = new VComboBox<>();

    private final VTextField eventName = new VTextField();
    private final RSyntaxTextArea methodBody = new RSyntaxTextArea();

    public TriggerImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        if (editingMode) {
            generator.setEnabled(false);
            triggerElementName.setEnabled(false);
        }

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        initConfiguration(new GridLayout(3, 2));

        addGeneratorConfiguration(generator);

        triggerElementName.setEditable(false);
        triggerElementName.setValidator(new NotEmptyValidator(triggerElementName::getSelectedItem));
        addConfigurationWithHelpEntry("trigger_element_name", triggerElementName);

        eventName.setValidator(() -> {
            if (eventName.getText() == null || eventName.getText().isEmpty()) {
                return new ValidationResult(ValidationResult.Type.ERROR, "Not empty");
            }
            return ValidationResult.PASSED;
        });
        addConfigurationWithHelpEntry("event_name", eventName);

        var toolbar = new JToolBar();
        JButton generate = new JButton(UIRES.get("18px.import"));
        generate.setToolTipText("Generate code");
        generate.addActionListener(e -> {
            var str = """
                    <#assign dependenciesCode>
                      <@procedureDependenciesCode dependencies, {
                            %map%
                      }/>
                    </#assign>
                    execute(event<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
                    """;
            str = str.replace("%map%", Objects.requireNonNull(getTriggerModElement().dependencies_provided).stream()
                    .map(a -> YamlUtils.keyAndValue(YamlUtils.str(a.getName()), YamlUtils.str(a.getName())))
                    .collect(Collectors.joining(",\n        ")));
            methodBody.setText(str);
        });
        toolbar.add(generate);
        var scrollpane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(methodBody, mcreator);
        AutoCompletionFactory.createDefaultCompletion(methodBody, this::createCompletionProvider);
        var panel = PanelUtils.northAndCenterElement(toolbar, scrollpane);
        panel.setBorder(BorderFactory.createTitledBorder("Body (ctrl+1 to auto complete)"));

        addPage(PanelUtils.northAndCenterElement(configurationPanel, panel)).validate(generator)
                .validate(triggerElementName).validate(eventName);
    }

    @Override protected void openInEditingMode(TriggerImplementationModElement generatableElement) {
        this.generator.setSelectedItem(generatableElement.generatorName);
        this.triggerElementName.setSelectedItem(generatableElement.triggerElementName);
        this.eventName.setText(generatableElement.eventName);
        this.methodBody.setText(generatableElement.methodBody);
    }

    @Override public TriggerImplementationModElement getElementFromGUI() {
        var element = new TriggerImplementationModElement(modElement);
        element.triggerElementName = triggerElementName.getSelectedItem();
        element.generatorName = generator.getSelectedItem();
        element.eventName = eventName.getText();
        element.methodBody = methodBody.getText();
        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    public TriggerModElement getTriggerModElement() {
        var trigger = mcreator.getWorkspace().getModElementByName(triggerElementName.getSelectedItem());
        if (trigger.getGeneratableElement() instanceof TriggerModElement triggerModElement) {
            return triggerModElement;
        }
        return null;
    }

    @Override public void reloadDataLists() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.TRIGGER.getRegistryName())) {
            stringArrayList.add(element.getName());
        }
        ComboBoxUtil.updateComboBoxContents(triggerElementName, stringArrayList);
    }

    private CompletionProvider createCompletionProvider() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.addCompletion(new BasicCompletion(provider, "dependencies"));
        provider.addCompletion(new BasicCompletion(provider, "${name}"));
        provider.addCompletion(new BasicCompletion(provider, "<#assign"));
        provider.addCompletion(new BasicCompletion(provider, "@procedureDependenciesCode"));
        provider.addCompletion(new BasicCompletion(provider, "execute()"));

        Utils.initCompletionWithGenerator(provider, mcreator.getGenerator());

        return provider;

    }
}
