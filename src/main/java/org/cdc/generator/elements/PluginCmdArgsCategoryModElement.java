package org.cdc.generator.elements;

import net.mcreator.workspace.elements.ModElement;

public class PluginCmdArgsCategoryModElement extends ProcedureCategoryModElement{
    public PluginCmdArgsCategoryModElement(ModElement element) {
        super(element);
    }

    @Override public String getBlocklyFolder() {
        return "cmdargs";
    }
}
