package org.cdc.generator.utils.interfaces;

import javax.swing.*;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * this interface provide the examples to be convenient to provide examples
 */
public interface IExamplesProvider {
    ServiceLoader<IExamplesProvider> examplesProviders = ServiceLoader.load(IExamplesProvider.class,
            IExamplesProvider.class.getClassLoader());

    void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer,String[] args);
}
