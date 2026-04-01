package org.cdc.generator.elements;

import com.google.j2objc.annotations.UsedByReflection;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.interfaces.IGeneratorElement;
import org.cdc.generator.utils.Constants;
import org.cdc.generator.utils.ElementsUtils;
import org.cdc.generator.utils.YamlUtils;

import java.beans.BeanProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class VariableImplementationModElement extends GeneratableElement implements IGeneratorElement {

    public String generator;
    public String variableElementName;
    public String defaultValue;

    public List<VariableScope> scopes;

    public VariableImplementationModElement(ModElement element) {
        super(element);
        scopes = new ArrayList<>();
    }

    @UsedByReflection public String getVariableName() {
        if (variableElementName == null) {
            return null;
        }
        return ElementsUtils.getVariableName(getModElement().getWorkspace(), variableElementName);
    }

    @UsedByReflection public String getDefaultValue() {
        return defaultValue;
    }

    @Override public String getGeneratorName() {
        return generator;
    }

    public static class VariableScope implements Cloneable {
        private String name;
        private String init;
        private String get;
        private String set;
        private String read;
        private String write;

        public VariableScope(String name) {
            this.name = name;
        }

        @BeanProperty(enumerationValues = { Constants.VariableScopes.LOCAL, Constants.VariableScopes.GLOBAL_MAP,
                Constants.VariableScopes.GLOBAL_SESSION, Constants.VariableScopes.GLOBAL_WORLD,
                Constants.VariableScopes.PLAYER_LIFETIME,
                Constants.VariableScopes.PLAYER_PERSISTENT }, description = "The name of scope")
        public void setName(String name) {
            this.name = name;
        }

        public void setInit(String init) {
            this.init = init;
        }

        public void setGet(String get) {
            this.get = get;
        }

        public void setSet(String set) {
            this.set = set;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public void setWrite(String write) {
            this.write = write;
        }

        public String getWrite() {
            return write;
        }

        public String getSet() {
            return set;
        }

        public String getRead() {
            return read;
        }

        @UsedByReflection public List<String> getReadLines() {
            return YamlUtils.splitString(read);
        }

        @UsedByReflection public List<String> getSetLines() {
            return YamlUtils.splitString(set);
        }

        @UsedByReflection public List<String> getWriteLines() {
            return YamlUtils.splitString(write);
        }

        public String getGet() {
            return get;
        }

        @UsedByReflection public List<String> getGetLines() {
            return YamlUtils.splitString(get);
        }

        public String getInit() {
            return init;
        }

        @UsedByReflection public List<String> getInitLines() {
            return YamlUtils.splitString(init);
        }

        public String getName() {
            return name;
        }

        @UsedByReflection public boolean hasNotNull() {
            return Stream.of(init, set, read, get, write).anyMatch(Objects::nonNull);
        }

        @Override public VariableScope clone() {
            try {
                VariableScope clone = (VariableScope) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
