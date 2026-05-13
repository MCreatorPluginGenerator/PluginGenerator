package org.cdc.generator.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.validation.component.VComboBox;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.VariableType;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class TypeListField extends JItemListField<String> {

    private Function<VariableType, String> getter;

    public TypeListField(MCreator mcreator, Function<VariableType, String> getter) {
        super(mcreator);

        this.getter = getter;
    }

    @Override protected List<String> getElementsToAdd() {
        var check = new VComboBox<VariableType>();
        check.setEditable(true);
        check.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(getter.apply((VariableType) value));
                return label;
            }
        });
        for (VariableType supportedType : Utils.getAllSupportedVariableTypes()) {
            check.addItem(supportedType);
        }
        var id = JOptionPane.showConfirmDialog(mcreator, check, "Select a type", JOptionPane.OK_CANCEL_OPTION);
        if (id == JOptionPane.OK_OPTION) {
            return List.of(getter.apply(check.getSelectedItem()));
        }
        return List.of();
    }
}
