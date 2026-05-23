package org.cdc.generator.utils.interfaces;

import org.cdc.generator.PluginMain;
import org.cdc.generator.utils.VariableType;

import java.util.List;
import java.util.ServiceLoader;

/**
 * To support other plugins.
 */
public interface ITypeProvider {
    ServiceLoader<ITypeProvider> serviceLoader = ServiceLoader.load(ITypeProvider.class, PluginMain.getINSTANCE()
            .getDependsClassLoader());

    List<VariableType> provide();
}
