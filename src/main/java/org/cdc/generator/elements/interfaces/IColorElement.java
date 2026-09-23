package org.cdc.generator.elements.interfaces;

import org.cdc.framework.annotaion.UsedByReflection;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.YamlUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface IColorElement {
    @Nullable String getBuiltinColor();

    Color getCustomColor();

    @UsedByReflection default String getColor() {
        if (getBuiltinColor() != null) {
            return YamlUtils.str(getBuiltinColor());
        }
        return Utils.convertColor(getCustomColor());
    }
}
