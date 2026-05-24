package org.cdc.generator.utils;

public class Constants {
    public static final String NONE = "(None)";
    public static final String[] mappingPlaceholders = new String[] { "@NAME", "@UPPERNAME", "@name", "@SnakeCaseName",
            "@registryname", "@REGISTRYNAME" };
    public static final String[] builtEntriesInDataList = new String[] { "_default", "_mcreator_map_template",
            "_bypass_prefix", "_suffix_separator" };

    public static class VariableScopes {
        public static final String LOCAL = "local";
        public static final String GLOBAL_SESSION = "global_session";
        public static final String GLOBAL_WORLD = "global_world";
        public static final String GLOBAL_MAP = "global_map";
        public static final String PLAYER_LIFETIME = "player_lifetime";
        public static final String PLAYER_PERSISTENT = "player_persistent";
    }

    public static class BuiltInColors {
        public static final String BKY_MATH_HUE = "%{BKY_MATH_HUE}";
        public static final String BKY_LOGIC_HUE = "%{BKY_LOGIC_HUE}";
        public static final String BKY_TEXTS_HUE = "%{BKY_TEXTS_HUE}";
    }
}
