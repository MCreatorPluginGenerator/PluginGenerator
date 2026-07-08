package org.cdc.generator.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import org.cdc.generator.utils.ElementsUtils;
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
        var check = new SearchableComboBox<String>();
        check.setModel(new AppendableComboBoxModel(check::addItem));
        for (VariableType supportedType : ElementsUtils.getAllSupportedVariableTypes()) {
            check.addItem(getter.apply(supportedType));
        }
        var id = JOptionPane.showConfirmDialog(mcreator, check, "Select a type", JOptionPane.OK_CANCEL_OPTION);
        if (id == JOptionPane.OK_OPTION) {
            return List.of(Objects.requireNonNull(check.getSelectedItem()));
        }
        return List.of();
    }
}
