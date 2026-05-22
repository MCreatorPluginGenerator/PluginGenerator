package org.cdc.generator.services.apis;

import net.mcreator.plugin.modapis.ModAPIImplementation;
import net.mcreator.plugin.modapis.ModAPIManager;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.interfaces.IAPIProvider;

import java.util.ArrayList;
import java.util.List;

public class CommonAPIProvider implements IAPIProvider {

    @Override public List<String> provide() {
        var list = new ArrayList<String>();
        for (String allSupportedGenerator : Utils.getAllSupportedGenerators()) {
            for (ModAPIImplementation modAPIImplementation : ModAPIManager.getModAPIsForGenerator(
                    allSupportedGenerator)) {
                list.add(modAPIImplementation.parent().id());
            }
        }
        return list;
    }
}
