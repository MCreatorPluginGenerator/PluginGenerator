package org.cdc.generator.utils.interfaces;

import org.cdc.generator.elements.PluginProcedureModElement;

import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public interface IProcedureBlock extends IPreviewable{
    List<String> getInputs();
    List<String> getFields();
    List<String> getStatements();
    List<PluginProcedureModElement.Dependency> getDependencies();
    String getParentFolder();
}
