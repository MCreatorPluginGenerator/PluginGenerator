package org.cdc.framework.utils;

import net.mcreator.ui.MCreator;

import java.util.LinkedHashMap;

public class L10NHelper {
    public static String getProcedureKey(String procedureName) {
        return "blockly.block." + procedureName;
    }

    public static String getTriggerKey(String triggerName) {
        return "trigger." + triggerName;
    }

    public static String getWarningKey(String warningKey) {
        return "blockly.warning." + warningKey;
    }

    /**
     * blockly.category.
     */
    public static String getBlocklyCategoryKey(String key) {
        return "blockly.category." + key;
    }

    public static LinkedHashMap<String, String> getDefaultLocalization(MCreator mcreator) {
        return mcreator.getWorkspace().getLanguageMap().get("en_us");
    }

    public static String getDefaultTranslation(MCreator mcreator, String key, String default1) {
        return getDefaultLocalization(mcreator).getOrDefault(key, default1);
    }
}

