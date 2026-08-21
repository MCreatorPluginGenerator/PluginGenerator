package org.cdc.generator.ui.renderer;

import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;

public class VariableTypeColorize extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        var labelText = jLabel.getText();
        this.setText(labelText);
        if (!isSelected && VariableTypeLoader.INSTANCE.doesVariableTypeExist(labelText)) {
            this.setForeground(VariableTypeLoader.INSTANCE.fromName(labelText).getBlocklyColor());
        }
        return this;
    }
}
