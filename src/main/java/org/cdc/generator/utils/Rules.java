package org.cdc.generator.utils;

import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import org.cdc.generator.PluginMain;
import org.cdc.generator.ui.preferences.PluginMakerPreference;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Rules {
    public static final Pattern DATALIST_ENTRY_NAME = Pattern.compile("[a-zA-Z_1-9.]+");
    public static final Pattern FILE_NAME = Pattern.compile("[a-z_]+");

    public static Validator getFileNameValidator(Supplier<String> supplier) {
        return () -> {
            if (Rules.FILE_NAME.matcher(Objects.requireNonNull(supplier.get())).matches()) {
                return ValidationResult.PASSED;
            }
            if (PluginMain.getINSTANCE().getApplication().getOpenMCreators().stream().anyMatch(a->supplier.get().contains(a.getWorkspaceSettings().getModID()))){
                return new ValidationResult(ValidationResult.Type.WARNING,"do not use modid");
            }
            return new ValidationResult(ValidationResult.Type.ERROR,
                    "You must use whole english and whole lower letters");
        };
    }

    public static class SearchRules {
        private static boolean ignoreCase;

        static {
            ignoreCase = PluginMakerPreference.INSTANCE.searchIgnoreCase.get();
        }

        private SearchRules() {

        }

        /**
         * Used by searchbar
         */
        public static String applyIgnoreCaseRule(String origin) {
            if (origin == null) {
                return "";
            }
            if (ignoreCase) {
                return origin.toLowerCase(Locale.ROOT);
            }
            return origin;
        }

        public static void setIgnoreCase(boolean ignoreCase1) {
            PluginMain.LOG.debug("Notify rule changed: {}->{}", ignoreCase, ignoreCase1);
            ignoreCase = ignoreCase1;
        }

        public static boolean isIgnoreCase() {
            return ignoreCase;
        }
    }

}
