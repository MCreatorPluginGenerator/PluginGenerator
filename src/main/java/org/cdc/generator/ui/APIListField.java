package org.cdc.generator.ui;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import org.cdc.generator.utils.ElementsUtils;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class APIListField extends JItemListField<String> {
    public APIListField(MCreator mcreator) {
        super(mcreator);
    }

    @Override protected List<String> getElementsToAdd() {
        var check = new SearchableComboBox<String>();
        check.setModel(new AppendableComboBoxModel(check::addItem));
        for (String api : ElementsUtils.getAllAPIS()) {
            check.addItem(api);
        }
        var id = JOptionPane.showConfirmDialog(mcreator, check, "Select a api", JOptionPane.OK_CANCEL_OPTION);
        if (id == JOptionPane.OK_OPTION) {
            return List.of(Objects.requireNonNull(check.getSelectedItem()));
        }
        return List.of();
    }
}
