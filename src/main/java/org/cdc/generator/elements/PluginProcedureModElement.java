package org.cdc.generator.elements;

import com.google.gson.JsonObject;
import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.services.types.ArgTypeProxy;
import org.cdc.generator.utils.Constants;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.YamlUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PluginProcedureModElement extends GeneratableElement implements IBlocklyElement {

    public List<ArgTypeProxy> arg0;
    public boolean inputsInline;
    public String previousStatement;
    public String nextStatement;
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

    @UsedByReflection public String getColor() {
        if (builtInColor != null){
            return YamlUtils.str(builtInColor);
        }
        return Utils.convertColor(colour);
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
            // mcreator will not check non-null.....So we only return a placeholder.
            return "Practice makes perfect";
        }
        return tooltip;
    }

    // compatible with previous version.
    // it may be null
    @UsedByReflection public List<String> getExtensions() {
        if (extensions == null) {
            return List.of();
        }
        return extensions;
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
