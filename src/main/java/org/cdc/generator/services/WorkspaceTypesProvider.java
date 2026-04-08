package org.cdc.generator.services;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.VariableModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.VariableType;
import org.cdc.generator.utils.interfaces.ITypeProvider;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceTypesProvider implements ITypeProvider {

    @Override public List<VariableType> provide() {
        return List.of();
    }

    @Override public List<VariableType> provide(MCreator mcreator) {
        var list = new ArrayList<VariableType>();
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.VARIABLE.getRegistryName())) {
            if (element.getGeneratableElement() instanceof VariableModElement modElement) {
                list.add(new VariableType(modElement.name, modElement.blocklyVariableType));
            }
        }
        return list;
    }
}
