package org.cdc.generator.elements;

import net.mcreator.workspace.elements.ModElement;

public class PluginAITasksProcedureModElement extends PluginProcedureModElement{
    public PluginAITasksProcedureModElement(ModElement element) {
        super(element);
    }

    @Override public String getBlocklyFolder() {
        return "aitasks";
    }
}
