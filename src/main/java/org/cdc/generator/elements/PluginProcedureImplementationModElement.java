package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.elements.interfaces.IGeneratorElement;

public class PluginProcedureImplementationModElement extends GeneratableElement implements IBlocklyElement,
        IGeneratorElement {

    public String generator;
    public String procedureFileName;

    public String content;

    public PluginProcedureImplementationModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getProcedureFileName() {
        return procedureFileName;
    }

    @Override public String getBlocklyFolder() {
        return "procedures";
    }

    @Override public String getGeneratorName() {
        return generator;
    }
}
