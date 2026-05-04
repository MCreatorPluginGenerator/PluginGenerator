package org.cdc.generator.elements;

import net.mcreator.workspace.elements.ModElement;

public class PluginAITasksCategoryModElement extends ProcedureCategoryModElement{
    public PluginAITasksCategoryModElement(ModElement element) {
        super(element);
    }

    @Override public String getBlocklyFolder() {
        return "aitasks";
    }
}
