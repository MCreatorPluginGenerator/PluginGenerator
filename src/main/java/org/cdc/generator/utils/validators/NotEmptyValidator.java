package org.cdc.generator.utils.validators;

import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;

import java.util.function.Supplier;

public class NotEmptyValidator implements Validator {
    private final Supplier<String> getter;

    public NotEmptyValidator(Supplier<String> getter) {
        this.getter = getter;
    }

    @Override public ValidationResult validate() {
        if (getter.get() != null && !getter.get().isBlank()) {
            return ValidationResult.PASSED;
        }
        return new ValidationResult(ValidationResult.Type.ERROR, "can not be empty");
    }
}
