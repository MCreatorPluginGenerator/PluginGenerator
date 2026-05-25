package org.cdc.generator.utils;

import net.mcreator.workspace.Workspace;
import org.cdc.generator.elements.VariableModElement;
import org.cdc.generator.utils.interfaces.IAPIProvider;
import org.cdc.generator.utils.interfaces.ITypeProvider;

import java.util.HashSet;
import java.util.Set;

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
     * @return apis
     */
    public static Set<String> getAllAPIS(){
        var set = new HashSet<String>();
        IAPIProvider.serviceLoader.stream().forEach(a -> {
            set.addAll(a.get().provide());
        });
        return set;
    }
}
