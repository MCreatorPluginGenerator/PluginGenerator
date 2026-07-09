package org.cdc.generator.ui.renderer;

import net.mcreator.ui.MCreator;
import org.cdc.framework.utils.L10NHelper;
import org.cdc.generator.ui.JCustomizeStringListField;

import javax.swing.*;
import java.awt.*;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/7/9
 */
public class CustomWarningStringListCellRenderer extends JCustomizeStringListField.CustomListCellRenderer {
    private final MCreator mcreator;

    public CustomWarningStringListCellRenderer(MCreator mcreator) {
        this.mcreator = mcreator;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        var key = L10NHelper.getWarningKey(label.getText());
        label.setText(L10NHelper.getDefaultTranslation(mcreator, key, key + "(missing translation)"));
        label.setToolTipText(key);
        return label;
    }
}
