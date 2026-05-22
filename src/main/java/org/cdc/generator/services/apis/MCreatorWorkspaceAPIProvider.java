package org.cdc.generator.services.apis;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.PluginMain;
import org.cdc.generator.elements.APIModElement;
import org.cdc.generator.init.ModElementTypes;
import org.cdc.generator.utils.interfaces.IAPIProvider;

import java.util.ArrayList;
import java.util.List;

public class MCreatorWorkspaceAPIProvider implements IAPIProvider {
    @Override public List<String> provide() {
        ArrayList<String> list = new ArrayList<>();
        for (MCreator openMCreator : PluginMain.getINSTANCE().getApplication().getOpenMCreators()) {
            provide(openMCreator,list);
        }
        return list;
    }

    public void provide(MCreator mcreator, List<String> list) {
        for (ModElement element : mcreator.getWorkspaceInfo()
                .getElementsOfType(ModElementTypes.APIS.getRegistryName())) {
            if (element.getGeneratableElement() instanceof APIModElement modElement) {
                list.add(modElement.getModElement().getRegistryName());
            }
        }
    }
}
