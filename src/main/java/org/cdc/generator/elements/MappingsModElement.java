package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IGeneratorElement;
import org.cdc.generator.utils.ElementsUtils;
import org.cdc.generator.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MappingsModElement extends GeneratableElement implements IGeneratorElement {

    public String generatorName;
    public String datalistElementName;
    public List<MappingEntry> mappingsContent;

    public MappingsModElement(ModElement element) {
        super(element);
    }

    @UsedByReflection @Override public String getGeneratorName() {
        return generatorName;
    }

    @UsedByReflection public String getDatalistName() {
        if (datalistElementName == null) {
            return null;
        }
        return ElementsUtils.getDataListName(getModElement().getWorkspace(), datalistElementName);
    }

    public static class MappingEntry implements Cloneable {
        private String name;
        private List<String> mappingContent;

        private boolean edited;

        public MappingEntry(String name, List<String> mappingContent) {
            this.name = name;
            this.mappingContent = mappingContent;
            this.edited = false;
        }

        public String getName() {
            return name;
        }

        public List<String> getMappingContent() {
            if (mappingContent == null)
                mappingContent = new ArrayList<>();
            return mappingContent;
        }

        @UsedByReflection public String getFirst() {
            return mappingContent.getFirst();
        }

        public void setEdited(boolean edited) {
            this.edited = edited;
        }

        public static boolean isEdited(String generatorName, String datalistName, MappingEntry entry) {
            var list = Utils.getMappingResult(generatorName, datalistName, entry.name);
            if (list != null) {
                return !list.equals(entry.getMappingContent());
            }
            return true;
        }

        public boolean isEdited() {
            return edited;
        }

        @Override public MappingEntry clone() {
            try {
                var mappingEntry = (MappingEntry) super.clone();
                mappingEntry.name = name;
                mappingEntry.mappingContent = new ArrayList<>(mappingContent);
                return mappingEntry;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
