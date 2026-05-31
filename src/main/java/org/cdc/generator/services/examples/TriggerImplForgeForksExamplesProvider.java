package org.cdc.generator.services.examples;

import jdk.jfr.Description;
import net.mcreator.ui.init.UIRES;
import org.cdc.framework.utils.BuilderUtils;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.ui.elements.TriggerImplementationModElementGUI;
import org.cdc.generator.utils.interfaces.IExamplesProvider;
import org.cdc.generator.utils.ioc.InjectField;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Description("TriggerImplExamples")
public class TriggerImplForgeForksExamplesProvider implements IExamplesProvider {
    Pattern methodNamePattern = Pattern.compile("(?<=\\.).+?(?=\\(\\))");

    @InjectField TriggerImplementationModElementGUI modElementGui;
    @Override
    public void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer,
            String[] args) {
        JButton generate = new JButton(UIRES.get("16px.forge"));
        generate.setToolTipText("Generate forge code");
        generate.addActionListener(e -> {
            var mappingEntries = modElementGui.getMappingEntries();
            var map = new HashMap<String,String>();
            for (TriggerModElement.Dependency dependency : modElementGui.getTriggerModElement().dependencies_provided) {
                if (mappingEntries.containsKey(dependency.getName())){
                    map.put(dependency.getName(),mappingEntries.get(dependency.getName()));
                } else {
                    map.put(dependency.getName(), dependency.getType());
                }
            }
            if (!modElementGui.getRelatedSourceText().isEmpty()){
                var java = Roaster.parse(modElementGui.getRelatedSourceText());
                if (java instanceof JavaClass<?> javaClass){
                    for (Map.Entry<String,String> value : map.entrySet()) {
                        var name1 = methodNamePattern.matcher(value.getValue());
                        if (name1.find()) {
                            var name = name1.group();
                            if (!javaClass.hasMethodSignature(name)) {
                                value.setValue("@Placeholder should check@" + value.getValue());
                            }
                        }
                    }
                }
            }
            var str = BuilderUtils.generateTriggerDependencies(map);
            exampleConsumer.accept(str);
        });
        componentConsumer.accept(generate);
    }
}
