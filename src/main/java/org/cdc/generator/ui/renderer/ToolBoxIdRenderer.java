package org.cdc.generator.ui.renderer;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import org.cdc.framework.utils.L10NHelper;
import org.cdc.generator.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ToolBoxIdRenderer extends DefaultListCellRenderer {

    private final MCreator mcreator;

    public ToolBoxIdRenderer(MCreator mcreator) {
        this.mcreator = mcreator;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        var labelText = jLabel.getText();
        if (Constants.NONE.equals(labelText)){
            return jLabel;
        }
        if (!PreferencesManager.PREFERENCES.ui.language.get().equals(L10N.DEFAULT_LOCALE)) {
            var key = L10NHelper.getBlocklyCategoryKey(labelText);
            var text =
                    Objects.requireNonNullElse(L10N.t(key), L10NHelper.getDefaultTranslation(mcreator, key, labelText))
                            + " - " + labelText;
            jLabel.setText(text);
        }
        jLabel.setToolTipText(labelText);
        return jLabel;
    }
}
