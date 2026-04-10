package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.elements.interfaces.IGeneratorElement;
import org.cdc.generator.utils.ElementsUtils;

public class PluginProcedureImplementationModElement extends GeneratableElement implements IBlocklyElement,
        IGeneratorElement {

    public String generator;
    public String pluginProcedureElementName;

    public String content;

    public PluginProcedureImplementationModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getProcedureFileName() {
        if (pluginProcedureElementName == null) {
            return null;
        }
        return ElementsUtils.getProcedureFileName(getModElement().getWorkspace(), pluginProcedureElementName);
    }

    @Override public String getBlocklyFolder() {
        return "procedures";
    }

    @Override public String getGeneratorName() {
        return generator;
    }
}
