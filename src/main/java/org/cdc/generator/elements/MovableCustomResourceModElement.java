package org.cdc.generator.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/6/14
 */
public class MovableCustomResourceModElement extends GeneratableElement {

    public String folder;
    public String content;

    public MovableCustomResourceModElement(ModElement element) {
        super(element);
    }

    public boolean isJson(){
        return folder.endsWith(".json") || folder.endsWith(".mcmeta");
    }

    public boolean isYaml(){
        return folder.endsWith(".yaml") || folder.endsWith(".yml");
    }

    public boolean isFile(){
        return !(isJson() || isYaml());
    }
}
