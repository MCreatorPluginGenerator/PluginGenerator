package org.cdc.generator.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.validation.component.VComboBox;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.VariableType;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TypeListField extends JItemListField<String> {

    private Function<VariableType, String> getter;

    public TypeListField(MCreator mcreator, Function<VariableType, String> getter) {
        super(mcreator);

        this.getter = getter;
    }

    @Override protected List<String> getElementsToAdd() {
        var check = new VComboBox<String>();
        check.setEditable(true);
        for (VariableType supportedType : Utils.getAllSupportedVariableTypes()) {
            check.addItem(getter.apply(supportedType));
        }
        var id = JOptionPane.showConfirmDialog(mcreator, check, "Select a type", JOptionPane.OK_CANCEL_OPTION);
        if (id == JOptionPane.OK_OPTION) {
            return List.of(Objects.requireNonNull(check.getSelectedItem()));
        }
        return List.of();
    }
}
