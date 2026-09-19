package org.cdc.generator.utils.decorators;

import net.mcreator.ui.MCreator;
import org.cdc.generator.elements.PluginProcedureModElement;
import org.cdc.generator.utils.interfaces.IProcedureBlock;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public class PluginProcedureModElementDecorator extends ModElementPreviewer implements IProcedureBlock {
    private static PluginProcedureModElementDecorator NULLINSTANCE;

    public static PluginProcedureModElementDecorator getNULLInstance() {
        if (NULLINSTANCE == null)
            NULLINSTANCE = new PluginProcedureModElementDecorator(null,null);
        return NULLINSTANCE;
    }

    private final PluginProcedureModElement pluginProcedureModElement;

    public PluginProcedureModElementDecorator(@Nullable PluginProcedureModElement pluginProcedureModElement, MCreator mCreator) {
        super(pluginProcedureModElement, mCreator);
        this.pluginProcedureModElement = pluginProcedureModElement;
    }

    @Override public List<String> getInputs() {
        if (pluginProcedureModElement == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(pluginProcedureModElement.inputs);
    }

    @Override public List<String> getFields() {
        if (pluginProcedureModElement == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(pluginProcedureModElement.fields);
    }

    @Override public List<String> getStatements() {
        if (pluginProcedureModElement == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(pluginProcedureModElement.statements);
    }

    @Override public List<PluginProcedureModElement.Dependency> getDependencies() {
        if (pluginProcedureModElement == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(pluginProcedureModElement.dependencies);
    }

    public PluginProcedureModElement getPluginProcedureModElement() {
        return pluginProcedureModElement;
    }

    @Override public String getParentFolder() {
        if (pluginProcedureModElement == null){
            return "";
        }
        return pluginProcedureModElement.getBlocklyFolder();
    }
}
