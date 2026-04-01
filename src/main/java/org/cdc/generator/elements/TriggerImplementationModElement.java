package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IGeneratorElement;
import org.cdc.generator.utils.ElementsUtils;
import org.cdc.generator.utils.YamlUtils;

import java.util.List;
import java.util.Locale;

public class TriggerImplementationModElement extends GeneratableElement implements IGeneratorElement {

    public String generatorName;
    public String triggerElementName;

    public String eventName;
    public String methodBody;

    public TriggerImplementationModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getTriggerName() {
        if (triggerElementName == null) {
            return null;
        }
        return ElementsUtils.getTriggerName(getModElement().getWorkspace(), triggerElementName);
    }

    @Override @UsedByReflection public String getGeneratorName() {
        return generatorName;
    }

    // a probable bug: if neoforge or forge change their event registration in new version, the solution will be invalid
    // so here needs a new solution.
    @UsedByReflection public String getGeneratorFlavor() {
        return generatorName.split("-")[0].toUpperCase(Locale.ROOT);
    }

    @UsedByReflection public String getEventNameUsedAsMethodName() {
        if (eventName.contains(".")) {
            return eventName.split("\\.")[0];
        }
        return eventName;
    }

    @UsedByReflection public List<String> getMethodBodyLines() {
        return YamlUtils.splitString(methodBody);
    }
}
