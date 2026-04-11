package org.cdc.framework.utils;

import com.google.gson.JsonArray;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.cdc.generator.utils.YamlUtils.*;

public class BuilderUtils {

    public static int countLanguageParameterCount(String text) {
        Pattern var = Pattern.compile("%\\d");
        var ma = var.matcher(text);
        int count = 0;
        while (ma.find()) {
            count++;
        }
        return count;
    }

    public static String getInputPlaceHolder(String name) {
        return "${input$" + name + "}";
    }

    public static String getStatementPlaceHolder(String name) {
        return "${statement$" + name + "}";
    }

    public static String getFieldPlaceHolder(String name) {
        return "${field$" + name + "}";
    }

    public static boolean isSupportProcedure(String generatorName) {
        return generatorName.startsWith("forge") || generatorName.startsWith("neoforge") || generatorName.startsWith(
                "fabric") || generatorName.startsWith("spigot");
    }

    public static String generateInputsComment(JsonArray inputs) {
        return inputs.asList().stream().map(a -> getInputPlaceHolder(a.getAsString()))
                .collect(Collectors.joining(",", "<#-- ", " -->"));
    }

    public static String generateStatementsComment(JsonArray statements) {
        return statements.asList().stream()
                .map(a -> getStatementPlaceHolder(a.getAsJsonObject().get("name").getAsString()))
                .collect(Collectors.joining(",", "<#-- ", " -->"));
    }

    public static String generateFieldsComment(JsonArray fields) {
        return fields.asList().stream().map(a -> getFieldPlaceHolder(a.getAsString()))
                .collect(Collectors.joining(",", "<#-- ", " -->"));
    }

    public static String generateTriggerDependencies(Map<String, String> dependencies) {
        return generateTriggerDependencies(dependencies, true);
    }

    public static String generateTriggerDependencies(Map<String, String> dependencies, boolean appendEvent) {
        String mapCode = dependencies.entrySet().stream()
                .map(entry -> keyAndValue(str(entry.getKey()), str(entry.getValue())))
                .collect(Collectors.joining(", " + lineSeparator + "\t\t\t"));
        return String.format("""
                		<#assign dependenciesCode><#compress>
                			<@procedureDependenciesCode dependencies, {
                			%s
                			}/>
                		</#compress></#assign>
                		execute(%s<#if dependenciesCode?has_content>,</#if>${dependenciesCode});
                """, mapCode, appendEvent ? "event" : "");
    }

}
