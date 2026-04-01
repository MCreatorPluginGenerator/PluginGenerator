package org.cdc.generator.utils.interfaces;

import java.util.List;
import java.util.ServiceLoader;

/**
 * To support other plugins.
 */
public interface ITypeProvider {
    ServiceLoader<ITypeProvider> serviceLoader = ServiceLoader.load(ITypeProvider.class,
            ITypeProvider.class.getClassLoader());

    List<String> provide();
}
