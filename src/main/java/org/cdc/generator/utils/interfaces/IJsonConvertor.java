package org.cdc.generator.utils.interfaces;

import com.google.gson.JsonObject;
import net.mcreator.element.GeneratableElement;
import org.cdc.generator.PluginMain;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

/**
 * convert json to my elements
 */
public interface IJsonConvertor<E extends GeneratableElement> {
    ServiceLoader<IJsonConvertor> serviceLoader = ServiceLoader.load(IJsonConvertor.class,
            PluginMain.getINSTANCE()
                    .getDependsClassLoader());

    default boolean matches(Path path){
        return Files.exists(path.getParent().resolve("plugin.json"));
    }

    E convert(JsonObject jsonObject, Path path);
}
