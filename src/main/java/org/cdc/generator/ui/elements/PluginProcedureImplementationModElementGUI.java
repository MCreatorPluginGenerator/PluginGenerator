package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.PluginProcedureImplementationModElement;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

public class PluginProcedureImplementationModElementGUI extends AbstractConfigurationTableModElementGUI<PluginProcedureImplementationModElement>{
    public PluginProcedureImplementationModElementGUI(MCreator mcreator, @NonNull ModElement modElement,
            boolean editingMode) {
        super(mcreator, modElement, editingMode, null);
    }

    @Override protected void initGUI() {

    }

    @Override protected void openInEditingMode(PluginProcedureImplementationModElement generatableElement) {

    }

    @Override public PluginProcedureImplementationModElement getElementFromGUI() {
        return null;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }
}
