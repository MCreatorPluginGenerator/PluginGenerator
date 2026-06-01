package org.cdc.generator.utils;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import org.cdc.generator.elements.VariableModElement;
import org.cdc.generator.utils.interfaces.IAPIProvider;
import org.cdc.generator.utils.interfaces.ITypeProvider;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class ElementsUtils {
    public static String getDataListName(Workspace workspace, String name) {
        var datalist = workspace.getModElementByName(name);
        if (datalist == null) {
            return null;
        }
        return datalist.getRegistryName();
    }

    public static String getVariableName(Workspace workspace, String name) {
        var variable = workspace.getModElementByName(name);
        if (variable == null) {
            return null;
        }
        if (variable.getGeneratableElement() instanceof VariableModElement variableModElement) {
            return variableModElement.name;
        }
        return variable.getRegistryName();
    }

    /**
     * @return all variable types in your mcreator
     */
    public static Set<VariableType> getAllSupportedVariableTypes() {
        // remove duplicated strs.
        var set = new HashSet<VariableType>();
        ITypeProvider.serviceLoader.stream().forEach(a -> {
            set.addAll(a.get().provide());
        });
        return set;
    }

    /**
     * This will check all mcreator instance.
     *
     * @return apis
     */
    public static Set<String> getAllAPIS() {
        var set = new HashSet<String>();
        IAPIProvider.serviceLoader.stream().forEach(a -> {
            set.addAll(a.get().provide());
        });
        return set;
    }

    public static String getExternalBlockColour(String blockName,
            BlocklyEditorType blocklyEditorType) {
        if (blocklyEditorType!= null) {
            var blocks = BlocklyLoader.INSTANCE.getBlockLoader(blocklyEditorType).getDefinedBlocks();
            if (blocks.containsKey(blockName)) {
                return blocks.get(blockName).getBlocklyJSON().get("colour").getAsString();
            }
        } else {
            AtomicReference<String> colour = new AtomicReference<>("");
            Stream.of(BlocklyEditorType.class.getFields()).forEach(a->{
                if (Modifier.isStatic(a.getModifiers())){
                    try {
                        BlocklyEditorType blocklyEditorType1 = (BlocklyEditorType) a.get(null);
                        var color = getExternalBlockColour(blockName, blocklyEditorType1);
                        if (!color.isEmpty())
                            colour.set(getExternalBlockColour(blockName, blocklyEditorType1));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return colour.get();
        }
        return "";
    }
}
