package org.cdc.generator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YamlUtils {
    public static String NULL = "null";
    public static String lineSeparator = "\n";

    public static String valuePrefix = "- ";
    public static String keySuffix = ": ";
    public static String multipleLines = "|";

    public static String str(String value) {
        return "\"" + value + "\"";
    }

    public static String keyAndValue(String key, String value) {
        return key + keySuffix + value;
    }

    public static List<String> splitString(String str) {
        if (str == null) {
            return List.of();
        }
        return new ArrayList<>(Arrays.asList(str.split(lineSeparator)));
    }
}
