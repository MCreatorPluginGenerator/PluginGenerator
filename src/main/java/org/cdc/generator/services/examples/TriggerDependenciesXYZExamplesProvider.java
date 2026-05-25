package org.cdc.generator.services.examples;

import jdk.jfr.Description;
import org.cdc.generator.utils.interfaces.IExamplesProvider;

import javax.swing.*;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/5/25
 */
@Description("TriggerDependencies")
public class TriggerDependenciesXYZExamplesProvider implements IExamplesProvider {

    @Override
    public void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer,
            String[] args) {
        JButton xyz = new JButton("XYZ");
        xyz.setContentAreaFilled(false);
        xyz.setToolTipText("Add xyz parameters");
        xyz.setOpaque(false);
        xyz.addActionListener(a -> {
            Stream.of("x", "y", "z").forEach(b -> {
                exampleConsumer.accept(Map.entry(b, "number"));
            });
        });
        componentConsumer.accept(xyz);
    }
}
