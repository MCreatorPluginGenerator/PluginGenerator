package org.cdc.generator.utils.interfaces;

import org.cdc.generator.elements.TriggerModElement;

import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public interface ITrigger extends IPreviewable{
    List<TriggerModElement.Dependency> getDependencies();
}
