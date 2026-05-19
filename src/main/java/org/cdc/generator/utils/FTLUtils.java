package org.cdc.generator.utils;

import java.util.Map;
import java.util.regex.Pattern;

public class FTLUtils {

    private static final Pattern cast = Pattern.compile("(/\\*@[a-zA-Z]+?\\*/)+");

    public static boolean isInputProcedure(String code) {
        return !code.endsWith("}") && !code.endsWith(";");
    }

    public static String getFileNameWithoutExtensions(String name) {
        return name.split("\\.")[0];
    }

    public static boolean isCombineCode(Map<String, Object> objectMap) {
        return objectMap.containsKey("head");
    }

    public static boolean isClass(String code) {
        if (code.contains(" class ")) {
            if (code.contains("import ")) {
                return true;
            }
            if (code.contains("/* imports omitted */")){
                return true;
            }
            return code.contains("package ");
        }
        return false;
    }

    public static String generateCodeHead(String code, String comment){
        var matcher = cast.matcher(code);
        var stringBuffer = new StringBuilder();

        // we must ensure that the /*@Blockstate*/ before the comment;
        if (matcher.find()){
            var str = matcher.group();
            matcher.appendReplacement(stringBuffer,str + comment);
            matcher.appendTail(stringBuffer);
        } else {
            stringBuffer.append(comment);
            stringBuffer.append(code);
        }
        return stringBuffer.toString();
    }
}
