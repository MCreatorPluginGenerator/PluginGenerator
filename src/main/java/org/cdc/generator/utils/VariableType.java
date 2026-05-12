package org.cdc.generator.utils;

public record VariableType(String name, String blocklyTypeName) {
    @Override public String toString() {
        return "VariableType{" + "name='" + name + '\'' + ", blocklyTypeName='" + blocklyTypeName + '\'' + '}';
    }
}
