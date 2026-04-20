package org.cdc.generator.utils;

import net.mcreator.workspace.Workspace;
import org.cdc.generator.elements.VariableModElement;

public class ElementsUtils {
    public static String getDataListName(Workspace workspace, String name) {
        var datalist = workspace.getModElementByName(name);
        if (datalist == null) {
            return null;
        }
        return datalist.getRegistryName();
    }

    public static String getProcedureFileName(Workspace workspace, String name) {
        var trigger = workspace.getModElementByName(name);
        if (trigger == null) {
            return null;
        }
        return trigger.getRegistryName();
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
}
