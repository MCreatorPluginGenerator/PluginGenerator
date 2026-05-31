package org.cdc.generator.services.examples;

import jdk.jfr.Description;
import net.mcreator.ui.init.UIRES;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.ui.elements.TriggerImplementationModElementGUI;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.InjectField;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaInterface;
import org.jboss.forge.roaster.model.Parameter;

import javax.swing.*;
import java.util.HashMap;
import java.util.function.Consumer;

@Description("TriggerImplExamples")
// Not the official generator
public class TriggerImplGoldenFabricExamplesProvider implements IExamplesProvider {
    @InjectField TriggerImplementationModElementGUI modElementGui;

    @Override
    public void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer,
            String[] args) {
        JButton generate = new JButton(UIRES.get("16px.fabric"));
        generate.setToolTipText("Generate golden fabric code");
        generate.addActionListener(e -> {
            var mappingEntries = modElementGui.getMappingEntries();
            var map = new HashMap<String, String>();
            for (TriggerModElement.Dependency dependency : modElementGui.getTriggerModElement().dependencies_provided) {
                if (mappingEntries.containsKey(dependency.getName())) {
                    map.put(dependency.getName(), mappingEntries.get(dependency.getName()));
                } else {
                    map.put(dependency.getName(), dependency.getType());
                }
            }
            String head = "(@Placeholder@" + String.join(",", map.keySet()) + """
                         ) -> {
                        """;
            if (!modElementGui.getRelatedSourceText().isEmpty()) {
                var type = Roaster.parse(modElementGui.getRelatedSourceText());
                if (type instanceof JavaInterface<?> javaInterface){
                    var first = javaInterface.getMethods().getFirst();
                    head = "(" + String.join(",", first.getParameters().stream().map(Parameter::getName).toList()) + """
                        ) -> {
                        """;
                }
            }
            var str = head + BuilderUtils.generateTriggerDependencies(map, false);
            exampleConsumer.accept(str);
        });
        componentConsumer.accept(generate);
    }
}
