package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TriggerModElement extends GeneratableElement {
    public String readableName;

    public List<String> required_apis;

    public List<Dependency> dependencies_provided;

    public boolean cancelable;
    public boolean has_result;
    public String side;

    public TriggerModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection public String getLowerLetterSide() {
        return side.toLowerCase(Locale.ROOT);
    }

    @UsedByReflection public String getSuggestedName() {
        if (readableName != null && !readableName.isBlank()) {
            return readableName;
        }
        return getModElement().getName();
    }

    public static class Dependency implements Cloneable {
        private String name;
        private String type;

        public Dependency(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override public Dependency clone() {
            try {
                Dependency clone = (Dependency) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
