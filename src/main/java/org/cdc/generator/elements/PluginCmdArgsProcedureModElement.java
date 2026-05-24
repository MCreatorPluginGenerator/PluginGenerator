package org.cdc.generator.elements;

import net.mcreator.workspace.elements.ModElement;

public class PluginCmdArgsProcedureModElement extends PluginProcedureModElement{
    public PluginCmdArgsProcedureModElement(ModElement element) {
        super(element);
    }

    @Override public String getBlocklyFolder() {
        return "cmdargs";
    }
}
