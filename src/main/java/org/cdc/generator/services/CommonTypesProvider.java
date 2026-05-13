package org.cdc.generator.services;

import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.utils.interfaces.ITypeProvider;

import java.util.ArrayList;
import java.util.List;

public class CommonTypesProvider implements ITypeProvider {
    @Override public List<org.cdc.generator.utils.VariableType> provide() {
        var list = new ArrayList<org.cdc.generator.utils.VariableType>();
        for (VariableType allVariableType : VariableTypeLoader.INSTANCE.getAllVariableTypes()) {
            list.add(new org.cdc.generator.utils.VariableType(allVariableType.getName(),allVariableType.getBlocklyVariableType()));
        }
        list.add(new org.cdc.generator.utils.VariableType("world","World"));
        list.add(new org.cdc.generator.utils.VariableType("diskrule","DiskRule"));
        list.add(new org.cdc.generator.utils.VariableType("null","Null"));
        //TODO: use prefergenerator to load hidden types.
        return list;
    }
}
