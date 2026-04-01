package org.cdc.generator.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataListModElement extends GeneratableElement {

    public boolean generateDataList;
    public String dialogMessage;

    public List<DataListEntry> entries;

    public DataListModElement(ModElement element) {
        super(element);
    }

    public String getDialogMessage() {
        if (dialogMessage != null && dialogMessage.isBlank()){
            return "empty";
        }
        return dialogMessage;
    }

    public static class DataListEntry implements Cloneable {
        public static DataListEntry copyValueOf(net.mcreator.minecraft.DataListEntry dataListEntry) {
            var dataListEntry1 = new DataListModElement.DataListEntry(dataListEntry.getName());
            dataListEntry1.readableName = dataListEntry.getReadableName();
            dataListEntry1.type = dataListEntry.getType();
            dataListEntry1.texture = dataListEntry.getTexture();
            dataListEntry1.description = dataListEntry.getDescription();
            var ma = new HashMap<String, String>();
            if (dataListEntry.getOther() instanceof Map<?, ?> map) {
                map.forEach((key, value) -> ma.put(key.toString(), value.toString()));
            }
            dataListEntry1.setOthers(ma);
            return dataListEntry1;
        }

        public static DataListEntry copyCommonValueOf(DataListEntry dataListEntry) {
            var datalistEntry1 = new DataListEntry(dataListEntry.name);
            datalistEntry1.readableName = dataListEntry.readableName;
            datalistEntry1.type = dataListEntry.type;
            datalistEntry1.texture = dataListEntry.texture;
            datalistEntry1.description = dataListEntry.description;
            datalistEntry1.setOthers(dataListEntry.others);
            datalistEntry1.setName(dataListEntry.name);
            return datalistEntry1;
        }

        private String name;
        // map readable_name
        private String readableName;
        private String type;
        private String texture;
        private String description;
        // map other
        private Map<String, String> others;

        private boolean builtIn;

        public DataListEntry(String name) {
            this.name = name;
            this.others = new HashMap<>();
        }

        public String getName() {
            return name;
        }

        public String getReadableName() {
            if (this.readableName != null && this.readableName.isBlank()) {
                return null;
            }
            return readableName;
        }

        public String getTexture() {
            if (this.texture != null && this.texture.isBlank()) {
                return null;
            }
            return texture;
        }

        public String getType() {
            if (this.type != null && this.type.isBlank()) {
                return null;
            }
            return type;
        }

        public Set<Map.Entry<String, String>> getOthers() {
            if (others == null) {
                others = new HashMap<>();
            }
            return others.entrySet();
        }

        public String getDescription() {
            return description;
        }

        public Map<String, String> getOther() {
            if (this.others == null) {
                this.others = new HashMap<>();
            }
            return others;
        }

        public void setName(String name) {
            this.name = name;
            if (name.charAt(0) == '_') {
                setBuiltIn(true);
                setReadableName(null);
                setDescription(null);
                setType(null);
                setTexture(null);
                setOthers(Map.of());
            }
        }

        public void setReadableName(String readable_name) {
            this.readableName = readable_name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTexture(String texture) {
            this.texture = texture;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setOthers(Map<String, String> others) {
            if (others == null) {
                this.others = new HashMap<>();
                return;
            }
            this.others = others;
        }

        public boolean isBuiltIn() {
            if (name.charAt(0) == '_')
                builtIn = true;
            return builtIn;
        }

        public void setBuiltIn(boolean builtIn) {
            this.builtIn = builtIn;
        }

        @Override public DataListEntry clone() {
            try {
                DataListEntry dataListEntry = (DataListEntry) super.clone();
                dataListEntry.others = new HashMap<>(others);
                dataListEntry.builtIn = builtIn;
                return dataListEntry;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public static class Builder {

            private final DataListEntry buildResult;

            public Builder(String name) {
                this.buildResult = new DataListEntry(name);
            }

            public Builder setReadableName(String readable_name) {
                this.buildResult.setReadableName(readable_name);
                return this;
            }

            public Builder setType(String type) {
                this.buildResult.setType(type);
                return this;
            }

            public Builder setTexture(String texture) {
                this.buildResult.setTexture(texture);
                return this;
            }

            public Builder setDescription(String description) {
                this.buildResult.setDescription(description);
                return this;
            }

            public Builder setOthers(Map<String, String> others) {
                this.buildResult.setOthers(others);
                return this;
            }

            public DataListEntry build() {
                return buildResult;
            }
        }
    }
}
