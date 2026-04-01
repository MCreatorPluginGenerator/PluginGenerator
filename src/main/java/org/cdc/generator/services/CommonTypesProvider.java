package org.cdc.generator.services;

import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.utils.interfaces.ITypeProvider;

import java.util.ArrayList;
import java.util.List;

public class CommonTypesProvider implements ITypeProvider {
    @Override public List<String> provide() {
        var list = new ArrayList<String>();
        for (VariableType allVariableType : VariableTypeLoader.INSTANCE.getAllVariableTypes()) {
            list.add(allVariableType.getName());
        }
        list.add("world");
        //TODO: use prefergenerator to load hidden types.
        return list;
    }
}
