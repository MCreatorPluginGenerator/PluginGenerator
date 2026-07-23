package org.cdc.generator.elements;

import net.mcreator.workspace.elements.ModElement;

public class PluginCustomCategoryModElement extends ProcedureCategoryModElement {

    public String parentFolder;

    public PluginCustomCategoryModElement(ModElement element) {
        super(element);
    }

    @Override public String getBlocklyFolder() {
        return parentFolder;
    }
}
