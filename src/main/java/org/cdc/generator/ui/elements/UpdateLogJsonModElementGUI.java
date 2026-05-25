package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.UpdateJsonModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/5/25
 */
public class UpdateLogJsonModElementGUI extends AbstractConfigurationTableModElementGUI<UpdateJsonModElement> {
    private JStringListField updateLogs;

    public UpdateLogJsonModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, new String[0]);

        updateLogs = new JStringListField(mcreator, null);

        configurationTitle = "Edit";

        this.initGUI();
        this.finalizeGUI();
    }

    @Override protected void initGUI() {
        updateLogs.disableItemCentering();
        updateLogs.setPreferredSize(Utils.tryToGetTextFieldSize());
        componentList.add(updateLogs);

        addPage(PanelUtils.totalCenterInPanel(buildConfiguration(1))).lazyValidate(()->{
            var elements = mcreator.getWorkspaceInfo().getElementsOfType(ModElementTypes.UPDATE_JSON_MOD_ELEMENT_MOD_ELEMENT_TYPE.getRegistryName()).stream().filter(a->!a.getName().equals(modElement.getName()));
            return elements.findAny().isPresent() ? new AggregatedValidationResult.FAIL("You can only have one updatelog element"):new AggregatedValidationResult.PASS();
        });
    }

    @Override protected void openInEditingMode(UpdateJsonModElement generatableElement) {
        updateLogs.setTextList(generatableElement.logs);
    }

    @Override public UpdateJsonModElement getElementFromGUI() {
        var element = new UpdateJsonModElement(modElement);
        element.logs = updateLogs.getTextList();
        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }
}
