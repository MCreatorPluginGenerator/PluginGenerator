package org.cdc.generator.utils.factories;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.ide.RSyntaxTextAreaStyler;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.*;

public class RSyntaxTextAreaFactory {
    public static RSyntaxTextArea createDefaultRSyntaxTextArea() {
        var jTextArea = new RSyntaxTextArea();
        jTextArea.setOpaque(false);
        jTextArea.setRows(15);
        jTextArea.setColumns(60);
        return jTextArea;
    }

    public static RTextScrollPane createDefaultTextScrollPane(RSyntaxTextArea jTextArea, Component parent) {
        RTextScrollPane jScrollPane = new RTextScrollPane(jTextArea);
        RSyntaxTextAreaStyler.style(jTextArea, jScrollPane, PreferencesManager.PREFERENCES.ide.fontSize.get());
        jScrollPane.getGutter().setFoldBackground(parent.getBackground());
        jScrollPane.getGutter().setBorderColor(parent.getBackground());
        return jScrollPane;
    }
}
