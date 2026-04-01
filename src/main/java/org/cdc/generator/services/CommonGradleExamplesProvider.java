package org.cdc.generator.services;

import jdk.jfr.Description;
import net.mcreator.ui.init.UIRES;
import org.cdc.generator.elements.APIModElement;
import org.cdc.generator.utils.interfaces.IExamplesProvider;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Consumer;

@Description("Gradles") public class CommonGradleExamplesProvider implements IExamplesProvider {

    @Override public void provideExamples(Consumer<JComponent> componentConsumer, Consumer<Object> exampleConsumer, String[] args) {
        JButton forge = new JButton(UIRES.get("16px.forge"));
        forge.setToolTipText("ForgeGradle");
        componentConsumer.accept(forge);
        JButton neo = new JButton(UIRES.get("16px.neoforge"));
        neo.setToolTipText("ModDevGradle");
        componentConsumer.accept(neo);
        JButton legacyNeo = new JButton(UIRES.get("16px.neoforge"));
        legacyNeo.setToolTipText("NeoLegacyGradle (Generator-1.20.1)");
        componentConsumer.accept(legacyNeo);

        forge.addActionListener(a -> {
            try (var stream = APIModElement.class.getResourceAsStream("/quilt-1.7.10/templates/apis/forgegradle.ftl")) {
                if (stream != null) {
                    exampleConsumer.accept(new String(stream.readAllBytes()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        neo.addActionListener(a -> {
            try (var stream = APIModElement.class.getResourceAsStream(
                    "/quilt-1.7.10/templates/apis/moddevgradle.ftl")) {
                if (stream != null) {
                    exampleConsumer.accept(new String(stream.readAllBytes()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        legacyNeo.addActionListener(a -> {
            try (var stream = APIModElement.class.getResourceAsStream(
                    "/quilt-1.7.10/templates/apis/legacymoddevgradle.ftl")) {
                if (stream != null) {
                    exampleConsumer.accept(new String(stream.readAllBytes()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
