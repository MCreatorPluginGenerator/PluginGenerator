package org.cdc.generator.elements;

import net.mcreator.workspace.elements.ModElement;

public class PluginCustomProcedureModElement extends PluginProcedureModElement{
    public String parentFolder;

    public PluginCustomProcedureModElement(ModElement element) {
        super(element);
    }

    @Override public String getBlocklyFolder() {
        return parentFolder;
    }
}
