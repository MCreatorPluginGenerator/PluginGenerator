package org.cdc.generator.utils.decorators;

import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.MCreator;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.utils.interfaces.IPreviewable;

import javax.annotation.Nullable;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/19
 */
public class ModElementPreviewer implements IPreviewable {
    protected final ModElement modElement;
    protected final MCreator mcreator;

    public ModElementPreviewer(@Nullable GeneratableElement generatableElement,MCreator mCreator){
        this(generatableElement == null?null:generatableElement.getModElement(),mCreator);
    }

    public ModElementPreviewer(ModElement modElement, MCreator mCreator) {
        this.modElement = modElement;
        this.mcreator = mCreator;
    }

    @Override public void openPreviewOrEdit() {
        if (modElement != null) {
            modElement.getType().getModElementGUI(mcreator, modElement, true).showView();
        }
    }

    @Override public String getName() {
        if (modElement == null){
            return "";
        }
        return modElement.getName();
    }
}
