package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.elements.interfaces.IColorElement;
import org.cdc.generator.services.types.ArgTypeProxy;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.YamlUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PluginProcedureModElement extends GeneratableElement implements IBlocklyElement, IColorElement {
    public List<ArgTypeProxy> arg0;
    public boolean inputsInline;
    public String previousStatement;
    public String nextStatement;
    public String mutator;
    public Color colour;
    public String builtInColor;
    public List<String> outputs;
    public List<String> extensions;

    // mcreator:
    // parent category
    public String toolbox_id;
    public List<String> toolbox_init;
    // sort group
    public String group;
    public List<Dependency> dependencies;
    public List<String> warnings;
    public List<String> required_apis;
    public List<String> inputs;
    public List<String> fields;
    public List<String> statements;

    public String localization;
    public String tooltip;

    public PluginProcedureModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getOutputs() {
        if (outputs.isEmpty()) {
            return null;
        }
        if (outputs.size() == 1) {
            return outputs.getFirst();
        }
        return "[" + outputs.stream().map(YamlUtils::str).collect(Collectors.joining(",")) + "]";
    }

    @Override public String getBlocklyFolder() {
        return "procedures";
    }

    @UsedByReflection public String getLocalization() {
        return localization;
    }

    @UsedByReflection public String getTooltip() {
        if (tooltip.isBlank()) {
            return PluginMakerPreference.INSTANCE.defaultProcedureTooltip.get();
        }
        return tooltip;
    }

    @UsedByReflection public String getMutator() {
        if (mutator != null && mutator.isBlank()) {
            return null;
        }
        return mutator;
    }

    // compatible with previous version.
    // it may be null
    @UsedByReflection public List<String> getExtensions() {
        if (extensions == null) {
            return List.of();
        }
        return extensions;
    }

    @Override public @Nullable String getBuiltinColor() {
        return builtInColor;
    }

    @Override public Color getCustomColor() {
        return colour;
    }

    public static class Dependency implements Cloneable {
        private String name;
        private String type;

        public Dependency(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public net.mcreator.blockly.data.Dependency toMCreatorDependency() {
            return new net.mcreator.blockly.data.Dependency(name, type);
        }

        @Override public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
