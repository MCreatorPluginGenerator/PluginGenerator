package org.cdc.generator.utils.interfaces;

import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.Workspace;
import org.cdc.generator.PluginMain;

import java.nio.file.Path;
import java.util.ServiceLoader;

/**
 * convert json to my elements
 */
public interface IJsonConvertor<E extends GeneratableElement> {
    ServiceLoader<IJsonConvertor> serviceLoader = ServiceLoader.load(IJsonConvertor.class,
            PluginMain.getINSTANCE()
                    .getDependsClassLoader());

    /**
     * @return true if the json file is the valid file related to the generable element.
     */
    boolean matches(Path path);

    /**
     *
     * @param jsonObject json
     * @return the element
     */
    E convert(JsonObject jsonObject, Workspace workspace);
}
