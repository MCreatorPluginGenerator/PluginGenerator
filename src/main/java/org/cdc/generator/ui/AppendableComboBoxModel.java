package org.cdc.generator.ui;

import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/7/8
 */
public class AppendableComboBoxModel extends DefaultComboBoxModel<String> {

    private final Consumer<String> add;
    private final Function<VTextField, Validator> validator;
    protected final String CUSTOM_ELEMENT = "+ Custom element";

    public AppendableComboBoxModel(Consumer<String> add) {
        this(add, null);
    }

    public AppendableComboBoxModel(Consumer<String> add, @Nullable Function<VTextField, Validator> validator) {
        this.add = add;
        this.validator = validator;
    }

    @Override public void setSelectedItem(Object anObject) {
        var index = getIndexOf(anObject);
        if (index == -1) {
            add.accept(Objects.toString(anObject));
        } else if (index == getSize() - 1) {
            var check = new VTextField();
            if (validator != null) {
                check.setValidator(validator.apply(check));
                check.enableRealtimeValidation();
            }
            var id = JOptionPane.showConfirmDialog(null, check, "input a element", JOptionPane.OK_CANCEL_OPTION);
            if (check.getValidationStatus() == null
                    || check.getValidationStatus() == ValidationResult.PASSED && id == JOptionPane.OK_OPTION) {
                var input = check.getText();
                if (!input.isEmpty()) {
                    add.accept(input);
                    super.setSelectedItem(input);
                }
            }
            return;
        }
        super.setSelectedItem(anObject);
    }

    @Override public void addElement(String anObject) {
        if (CUSTOM_ELEMENT.equals(anObject)) {
            return;
        }
        super.addElement(anObject);
    }

    @Override public int getSize() {
        return super.getSize() + 1;
    }

    @Override public String getElementAt(int index) {
        if (index < super.getSize()) {
            return super.getElementAt(index);
        } else {
            return CUSTOM_ELEMENT;
        }
    }

    @Override public int getIndexOf(Object anObject) {
        if (CUSTOM_ELEMENT.equals(anObject)) {
            return getSize() - 1;
        }
        return super.getIndexOf(anObject);
    }
}
