package org.cdc.generator.utils.interfaces;

import net.mcreator.ui.MCreator;
import org.cdc.generator.PluginMain;

import javax.swing.*;
import java.util.ServiceLoader;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/6/8
 */
public interface IMenusProvider {
    ServiceLoader<IMenusProvider> serviceLoader = ServiceLoader.load(IMenusProvider.class, PluginMain.getINSTANCE()
            .getDependsClassLoader());

    JMenu provide(MCreator mCreator);
}
