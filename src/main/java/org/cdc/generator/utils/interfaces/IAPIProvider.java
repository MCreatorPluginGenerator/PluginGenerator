package org.cdc.generator.utils.interfaces;

import java.util.List;
import java.util.ServiceLoader;

public interface IAPIProvider {
    ServiceLoader<IAPIProvider> serviceLoader = ServiceLoader.load(IAPIProvider.class, IAPIProvider.class.getClassLoader());

    List<String> provide();
}
