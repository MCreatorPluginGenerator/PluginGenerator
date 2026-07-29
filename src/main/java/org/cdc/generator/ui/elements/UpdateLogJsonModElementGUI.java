package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.UpdateJsonModElement;
import org.cdc.generator.init.ModElementTypes;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/5/25
 */
public class UpdateLogJsonModElementGUI extends AbstractConfigurationTableModElementGUI<UpdateJsonModElement> {
    private JTextArea updateLogs;

    public UpdateLogJsonModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[0]);

        updateLogs = new JTextArea();

        configurationTitle = "Edit changelog";

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        updateLogs.setColumns(50);
        updateLogs.setRows(4);
        componentList.add(new JScrollPane(updateLogs));

        addPage(PanelUtils.totalCenterInPanel(buildConfiguration(1))).lazyValidate(() -> {
            var elements = mcreator.getWorkspaceInfo()
                    .getElementsOfType(ModElementTypes.UPDATE_JSON_MOD_ELEMENT_MOD_ELEMENT_TYPE.getRegistryName())
                    .stream().filter(a -> !a.getName().equals(modElement.getName()));
            return elements.findAny().isPresent() ?
                    new AggregatedValidationResult.FAIL("You can only have one updatelog element") :
                    new AggregatedValidationResult.PASS();
        });
    }

    @Override protected void openInEditingMode(UpdateJsonModElement generatableElement) {
        updateLogs.setText(String.join("\n", generatableElement.logs));
    }

    @Override public UpdateJsonModElement getElementFromGUI() {
        var element = new UpdateJsonModElement(modElement);
        if (updateLogs.getText().isBlank()) {
            element.logs = List.of();
        } else {
            element.logs = List.of(updateLogs.getText().split("\n"));
        }

        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }

    @Override public ViewBase showView() {
        return super.showView();
    }
}
