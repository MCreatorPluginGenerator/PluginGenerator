package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.utils.Utils;

import java.awt.*;

public class ProcedureCategoryModElement extends GeneratableElement implements IBlocklyElement {

    public String readableName;

    public Color color;
    // mapped parent_category
    public String parentCategory;
    public boolean api;

    public ProcedureCategoryModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getColor() {
        return Utils.convertColor(color);
    }

    @UsedByReflection public String getParentCategory() {
        if (parentCategory != null && parentCategory.isBlank()) {
            return null;
        }
        return parentCategory;
    }

    @Override public String getBlocklyFolder() {
        return "procedures";
    }

    @UsedByReflection public String getSuggestedName() {
        if (readableName != null && !readableName.isBlank()) {
            return readableName;
        }
        return getModElement().getName();
    }
}
