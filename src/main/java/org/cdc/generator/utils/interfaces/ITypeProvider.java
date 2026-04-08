package org.cdc.generator.utils.interfaces;

import net.mcreator.ui.MCreator;
import org.cdc.generator.utils.VariableType;

import java.util.List;
import java.util.ServiceLoader;

/**
 * To support other plugins.
 */
public interface ITypeProvider {
    ServiceLoader<ITypeProvider> serviceLoader = ServiceLoader.load(ITypeProvider.class,
            ITypeProvider.class.getClassLoader());

    List<VariableType> provide();

    default List<VariableType> provide(MCreator mCreator){
        return provide();
    }
}
