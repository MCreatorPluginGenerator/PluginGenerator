package org.cdc.generator.utils;

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import org.cdc.generator.PluginMain;
import org.cdc.generator.ui.preferences.PluginMakerPreference;
import org.cdc.generator.utils.validators.RegistryNameValidator;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Rules {
    public static final Pattern DATALIST_ENTRY_NAME = Pattern.compile("[a-zA-Z_1-9.]+");
    public static final Pattern FILE_NAME = Pattern.compile("[a-z_]+");
    public static final Pattern VALID_MODID = Pattern.compile("^(?=.{2,64}$)[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$");

    public static final int defaultHsvSaturation = 45;
    public static final int defaultHsvValue = 65;

    public static Validator getFileNameValidator(Supplier<String> supplier) {
        return () -> {
            if (Rules.FILE_NAME.matcher(Objects.requireNonNull(supplier.get())).matches()) {
                return ValidationResult.PASSED;
            }
            return new ValidationResult(ValidationResult.Type.ERROR,
                    "You must use whole english and whole lower letters");
        };
    }

    public static Validator getModidValidator(Supplier<String> getter) {
        return new Validator() {
            private final Validator parent = new RegistryNameValidator(getter,
                    L10N.t("dialog.workspace.settings.workspace_modid")).setMaxLength(32);

            @Override public ValidationResult validate() {
                if (!Rules.VALID_MODID.matcher(getter.get()).matches())
                    return new ValidationResult(ValidationResult.Type.ERROR,
                            L10N.t("dialog.workspace.settings.workspace_modid_invalid"));
                return parent.validate();
            }
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
