package org.cdc.generator.utils.interfaces;

import org.cdc.generator.PluginMain;

import java.util.List;
import java.util.ServiceLoader;

public interface IAPIProvider {
    ServiceLoader<IAPIProvider> serviceLoader = ServiceLoader.load(IAPIProvider.class, PluginMain.getINSTANCE()
            .getDependsClassLoader());

    List<String> provide();
}
