package org.cdc.generator.services;

import jdk.jfr.Description;
import net.mcreator.util.DesktopUtils;
import org.cdc.generator.elements.APIModElement;
import org.cdc.generator.utils.interfaces.IExamplesProvider;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

@Description("Gradles")
// It is cursed....
public class CurseMavenExamplesProvider implements IExamplesProvider {

    @Override public void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer, String[] args) {
        JButton curseMaven = new JButton("Curse");
        curseMaven.setToolTipText("CurseMaven");
        componentConsumer.accept(curseMaven);
        curseMaven.addActionListener(a -> {
            DesktopUtils.browseSafe("https://cursemaven.com");
            try (var stream = APIModElement.class.getResourceAsStream("/quilt-1.7.10/templates/apis/cursemaven.ftl")) {
                if (stream != null) {
                    exampleConsumer.accept(new String(stream.readAllBytes()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
