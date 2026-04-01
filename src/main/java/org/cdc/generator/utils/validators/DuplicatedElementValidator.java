package org.cdc.generator.utils.validators;

import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DuplicatedElementValidator implements Validator, Supplier<AggregatedValidationResult> {

    // unique id list
    private final Supplier<List<String>> uidsGetter;
    private final Consumer<Integer> notifier;

    public DuplicatedElementValidator(Supplier<List<String>> uidsGetter, Consumer<Integer> notifier) {
        this.uidsGetter = uidsGetter;
        this.notifier = notifier;
    }

    @Override public ValidationResult validate() {
        Set<String> names = new HashSet<>();
        var uids = uidsGetter.get();
        for (int i = 0; i < uids.size(); i++) {
            if (!names.contains(uids.get(i))) {
                names.add(uids.get(i));
            } else {
                notifier.accept(i);
                return new ValidationResult(ValidationResult.Type.ERROR, "Duplicated keys exists");
            }
        }
        return ValidationResult.PASSED;
    }

    @Override public AggregatedValidationResult get() {
        Set<String> names = new HashSet<>();
        var uids = uidsGetter.get();
        for (int i = 0; i < uids.size(); i++) {
            if (!names.contains(uids.get(i))) {
                names.add(uids.get(i));
            } else {
                notifier.accept(i);
                return new AggregatedValidationResult.FAIL("Duplicated keys exists");
            }
        }
        return new AggregatedValidationResult.PASS();
    }
}
