package org.cdc.generator.utils.decorators;

import net.mcreator.ui.MCreator;
import org.cdc.generator.elements.TriggerModElement;
import org.cdc.generator.utils.interfaces.ITrigger;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public class TriggerDecorator extends ModElementPreviewer implements ITrigger {
    private static TriggerDecorator INSTANCE;

    public static TriggerDecorator getNULLInstance() {
        if (INSTANCE == null)
            INSTANCE = new TriggerDecorator(null,null);
        return INSTANCE;
    }

    private final TriggerModElement triggerModElement;

    public TriggerDecorator(@Nullable TriggerModElement generatableElement, MCreator mCreator) {
        super(generatableElement, mCreator);
        this.triggerModElement = generatableElement;
    }

    @Override public List<TriggerModElement.Dependency> getDependencies() {
        if (triggerModElement == null){
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(triggerModElement.dependencies_provided);
    }
}
