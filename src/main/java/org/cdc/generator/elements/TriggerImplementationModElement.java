package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import org.cdc.generator.elements.interfaces.IGeneratorElement;
import org.cdc.generator.elements.interfaces.IUniqueElement;
import org.cdc.generator.utils.YamlUtils;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

public class TriggerImplementationModElement extends GeneratableElement implements IGeneratorElement, IUniqueElement {

    public String generatorName;
    public String triggerFileName;
    @ModElementReference public String searchable;
    public boolean enableCustom;

    public String eventName;
    public String methodBody;

    public ArrayList<AbstractMap.SimpleEntry<String, String>> mappingEntries;
    public String relatedClassSource;

    public TriggerImplementationModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getTriggerName() {
        return triggerFileName;
    }

    @Override @UsedByReflection public String getGeneratorName() {
        return generatorName;
    }

    // a probable bug: if neoforge or forge change their event registration in new version, the solution will be invalid
    // so here needs a new solution.
    @UsedByReflection public String getGeneratorFlavor() {
        if (enableCustom) {
            return "CUSTOM";
        }
        return generatorName.split("-")[0].toUpperCase(Locale.ROOT);
    }

    @UsedByReflection public String getEventNameUsedAsMethodName() {
        if (eventName.contains(".")) {
            var sp = eventName.split("\\.");
            return Arrays.stream(sp).filter(a -> a.endsWith("Event") || List.of("Pre", "Post").contains(a))
                    .collect(Collectors.joining());
        }
        return eventName;
    }

    @UsedByReflection public List<String> getMethodBodyLines() {
        return YamlUtils.splitString(methodBody);
    }

    @Override public BufferedImage generateModElementPicture() {
        return IGeneratorElement.super.generateModElementPicture0();
    }

    @Override public String getUniqueID() {
        return getModElement().getTypeString() + getGeneratorName() + triggerFileName;
    }
}
