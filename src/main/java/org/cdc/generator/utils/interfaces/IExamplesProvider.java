package org.cdc.generator.utils.interfaces;

import org.cdc.generator.PluginMain;

import javax.swing.*;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * this interface provide the examples to be convenient to provide examples
 */
public interface IExamplesProvider {
    ServiceLoader<IExamplesProvider> examplesProviders = ServiceLoader.load(IExamplesProvider.class,
            PluginMain.getINSTANCE()
                    .getDependsClassLoader());

    void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer,String[] args);
}
