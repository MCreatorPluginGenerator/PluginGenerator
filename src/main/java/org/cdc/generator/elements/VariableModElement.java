package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.utils.Utils;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class VariableModElement extends GeneratableElement {
    public boolean generate;
    // like actionresult
    public String name;
    public Color color;
    // use this will ignore color
    public String strColor;
    // like ActionResult
    public String blocklyVariableType;
    public boolean ignoredByCoverage;
    public boolean nullable;

    @Nullable public List<String> required_apis;

    public VariableModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getFormattedColor() {
        if (strColor != null) {
            return "\"" + strColor + "\"";
        }
        return Utils.convertColor(color);
    }

    public String getName() {
        return name;
    }

    @UsedByReflection public boolean isGenerate() {
        return generate;
    }
}
