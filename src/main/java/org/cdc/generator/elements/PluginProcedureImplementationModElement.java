package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import org.cdc.generator.elements.interfaces.IBlocklyElement;
import org.cdc.generator.elements.interfaces.IGeneratorElement;
import org.cdc.generator.elements.interfaces.IUniqueElement;

import java.awt.image.BufferedImage;

public class PluginProcedureImplementationModElement extends GeneratableElement
        implements IBlocklyElement, IGeneratorElement, IUniqueElement {

    public String generator;
    public String procedureFileName;

    @ModElementReference public String searchable;

    public boolean isTemplate;
    public String templateFolder;

    public String content;
    public String procedureFolder;

    public PluginProcedureImplementationModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getProcedureFileName() {
        return procedureFileName;
    }

    @Override public String getBlocklyFolder() {
        if (procedureFolder == null) {
            procedureFolder = "procedures";
        }
        if (isTemplate) {
            return getCombinedProcedureFolder();
        }
        return procedureFolder;
    }

    @Override public String getGeneratorName() {
        return generator;
    }

    public String getCombinedProcedureFolder(){
        if (templateFolder != null && !templateFolder.isEmpty()) {
            return procedureFolder + "/utils/" + templateFolder;
        }
        return procedureFolder + "/utils";
    }

    @Override public BufferedImage generateModElementPicture() {
        return IGeneratorElement.super.generateModElementPicture0();
    }

    @Override public String getUniqueID() {
        return getModElement().getTypeString() + getGeneratorName() + procedureFileName + isTemplate + procedureFolder;
    }
}
