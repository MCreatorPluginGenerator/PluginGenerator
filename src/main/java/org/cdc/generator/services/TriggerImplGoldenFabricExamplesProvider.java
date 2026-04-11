package org.cdc.generator.services;

import jdk.jfr.Description;
import net.mcreator.ui.init.UIRES;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.ui.elements.TriggerImplementationModElementGUI;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.InjectField;

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
            var map = new HashMap<String, String>();
            for (TriggerModElement.Dependency dependency : modElementGui.getTriggerModElement().dependencies_provided) {
                map.put(dependency.getName(), dependency.getType());
            }
            var str = """
                    (@Placeholder world,pos and so on@) -> {
                    """ + BuilderUtils.generateTriggerDependencies(map, false);
            exampleConsumer.accept(str);
        });
        componentConsumer.accept(generate);
    }
}
