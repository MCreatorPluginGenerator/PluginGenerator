package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.utils.YamlUtils;

import java.util.ArrayList;
import java.util.List;

public class APIModElement extends GeneratableElement {
    public String apiName;

    public List<Configuration> configurations;

    public APIModElement(ModElement element) {
        super(element);
    }

    public static class Configuration implements Cloneable {
        private String generator;
        private boolean requiredWhenEnable;

        private String gradle;
        private List<String> updateFiles;
        private String versionRange;
        //TODO How to use it?
        private String resource_paths;

        public Configuration() {
            this.updateFiles = new ArrayList<>();
            this.gradle = """
                    repositories {
                      maven {
                        // location of the maven that hosts api
                        // This is a example of JEI
                        name = "Progwml6 maven"
                        url = "https://dvs1.progwml6.com/files/maven/"
                      }
                    }
                    dependencies {
                    }
                    """;
        }

        public String getGenerator() {
            return generator;
        }

        public List<String> getUpdateFiles() {
            return updateFiles;
        }

        public String getGradle() {
            return gradle;
        }

        @UsedByReflection public List<String> getYamlGradle() {
            return YamlUtils.splitString(this.gradle);
        }

        public String getVersionRange() {
            if (versionRange != null && versionRange.isBlank()) {
                return null;
            }
            return versionRange;
        }

        public boolean isRequiredWhenEnable() {
            return requiredWhenEnable;
        }

        public void setGenerator(String generator) {
            this.generator = generator;
        }

        public void setRequiredWhenEnable(boolean requiredWhenEnable) {
            this.requiredWhenEnable = requiredWhenEnable;
        }

        public void setGradle(String gradle) {
            this.gradle = gradle;
        }

        @UsedByReflection public void setUpdateFiles(List<String> updateFiles) {
            this.updateFiles = updateFiles;
        }

        public void setVersionRange(String versionRange) {
            this.versionRange = versionRange;
        }

        @Override public Configuration clone() {
            try {
                Configuration clone = (Configuration) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
