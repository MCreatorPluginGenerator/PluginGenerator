package org.cdc.generator.utils;

public class Constants {
    public static final String NONE = "None";
    public static final String[] mappingPlaceholders = new String[] { "@NAME", "@UPPERNAME", "@name", "@SnakeCaseName",
            "@registryname", "@REGISTRYNAME" };
    public static final String[] builtEntriesInDataList = new String[] { "_default", "_mcreator_map_template",
            "_bypass_prefix" };

    public static class VariableScopes {
        public static final String LOCAL = "local";
        public static final String GLOBAL_SESSION = "global_session";
        public static final String GLOBAL_WORLD = "global_world";
        public static final String GLOBAL_MAP = "global_map";
        public static final String PLAYER_LIFETIME = "player_lifetime";
        public static final String PLAYER_PERSISTENT = "player_persistent";
    }

    public static class Generators {
        public static final String FORGE1201 = "forge-1.20.1";

        public static final String NEOFORGE1211 = "neoforge-1.21.1";
        public static final String NEOFORGE1214 = "neoforge-1.21.4";
        public static final String NEOFORGE1218 = "neoforge-1.21.8";

        public static final String DATAPACK1211 = "datapack-1.21.1";
        public static final String DATAPACK1214 = "datapack-1.21.4";

        public static final String SPIGOT1214 = "spigot-1.21.4";
    }

    public static class BuiltInColors {
        public static final String BKY_MATH_HUE = "%{BKY_MATH_HUE}";
        public static final String BKY_LOGIC_HUE = "%{BKY_LOGIC_HUE}";
        public static final String BKY_TEXTS_HUE = "%{BKY_TEXTS_HUE}";

        public static final String ENTITY_COLOR = "195";
        public static final String ITEMSTACK_COLOR = "250";
        public static final String BLOCK_COLOR = "60";
        public static final String ENERGY_FLUID_COLOR = "60";
        public static final String DAMAGE_SOURCE_COLOR = "320";
        public static final String DIRECTION_ACTION_COLOR = "20";
        public static final String GUI_MANAGEMENT_COLOR = "110";
        public static final String PLAYER_COLOR = "175";
        public static final String PROJECTILE_COLOR = "300";
        public static final String WORLD_COLOR = "35";
    }
}
