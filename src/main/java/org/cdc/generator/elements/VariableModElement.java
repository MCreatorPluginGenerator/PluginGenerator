package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IColorElement;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class VariableModElement extends GeneratableElement implements IColorElement {
    public boolean generate;
    // like actionresult
    public String name;
    public Color color;
    // use this will ignore color
    public String builtinColor;
    // like ActionResult
    public String blocklyVariableType;
    public boolean ignoredByCoverage;
    public boolean nullable;
    public String customVariableDependencyLocalization;

    @Nullable public List<String> required_apis;

    public VariableModElement(ModElement element) {
        super(element);
    }

    public String getName() {
        return name;
    }

    @UsedByReflection public boolean isGenerate() {
        return generate;
    }

    @UsedByReflection public String getCustomVariableDependencyLocalization() {
        return customVariableDependencyLocalization;
    }

    @Override public @org.jetbrains.annotations.Nullable String getBuiltinColor() {
        return builtinColor;
    }

    @Override public Color getCustomColor() {
        return color;
    }
}
