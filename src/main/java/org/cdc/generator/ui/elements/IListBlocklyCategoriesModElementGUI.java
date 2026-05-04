package org.cdc.generator.ui.elements;

import net.mcreator.ui.blockly.BlocklyEditorType;
import org.cdc.generator.elements.interfaces.IBlocklyCategoryElement;

public interface IListBlocklyCategoriesModElementGUI {
    BlocklyEditorType getBlocklyEditorType();

    Class<? extends IBlocklyCategoryElement> getBlocklyCategoryClass();

    boolean hasBuiltinCategories();
}
