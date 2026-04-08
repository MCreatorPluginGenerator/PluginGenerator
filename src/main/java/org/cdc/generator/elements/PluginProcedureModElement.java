package org.cdc.generator.elements;

import com.google.gson.JsonObject;
import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.YamlUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PluginProcedureModElement extends GeneratableElement implements IBlocklyElement {

    public List<JsonObject> arg0;
    public boolean inputsInline;
    public String previousStatement;
    public String nextStatement;
    public Color colour;
    public List<String> outputs;

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

    public PluginProcedureModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getColor() {
        return Utils.convertColor(colour);
    }

    @UsedByReflection public String getOutputs() {
        if (outputs.isEmpty()){
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
