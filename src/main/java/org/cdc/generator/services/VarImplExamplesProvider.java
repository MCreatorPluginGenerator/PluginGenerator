package org.cdc.generator.services;

import jdk.jfr.Description;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.Inject;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

@Description("VarImplExamples") public class VarImplExamplesProvider implements IExamplesProvider {

    @Inject String generatorName;
    @Inject String name;
    @Inject String columnName;

    @Override
    public void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer,
            String[] args) {
        JButton number = new JButton("Number");
        number.setOpaque(false);
        number.addActionListener(a -> {
            var generator = Generator.GENERATOR_CACHE.get(generatorName);
            if (generator != null) {
                var num = getVariableScope("number", name, columnName, generator);
                exampleConsumer.accept(String.join("\n", num));
            }
        });
        componentConsumer.accept(number);
        JButton logic = new JButton("Logic");
        logic.setOpaque(false);
        logic.addActionListener(a -> {
            var generator = Generator.GENERATOR_CACHE.get(generatorName);
            if (generator != null) {
                var num = getVariableScope("logic", name, columnName, generator);
                exampleConsumer.accept(String.join("\n", num));
            }
        });
        componentConsumer.accept(logic);
    }

    private static @NonNull List<String> getVariableScope(String variableName, String scopeName, String phaseName,
            GeneratorConfiguration generator) {
        return Utils.convertYamlToList(generator.getVariableTypes()
                .getScopeDefinition(VariableTypeLoader.INSTANCE.fromName(variableName), scopeName).get(phaseName));
    }
}
