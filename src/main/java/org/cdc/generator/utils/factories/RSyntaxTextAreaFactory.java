package org.cdc.generator.utils.factories;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.ide.RSyntaxTextAreaStyler;
import net.mcreator.util.DesktopUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.net.URISyntaxException;

public class RSyntaxTextAreaFactory {
    public static RSyntaxTextArea createDefaultRSyntaxTextArea() {
        var jTextArea = new RSyntaxTextArea();
        jTextArea.setOpaque(false);
        jTextArea.setRows(15);
        jTextArea.setColumns(60);
        jTextArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    DesktopUtils.browse(e.getURL().toURI());
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return jTextArea;
    }

    public static RTextScrollPane createDefaultTextScrollPane(RSyntaxTextArea jTextArea, Component parent) {
        RTextScrollPane jScrollPane = new RTextScrollPane(jTextArea,
                PreferencesManager.PREFERENCES.ide.lineNumbers.get());
        RSyntaxTextAreaStyler.style(jTextArea, jScrollPane, PreferencesManager.PREFERENCES.ide.fontSize.get());
        jScrollPane.getGutter().setFoldBackground(parent.getBackground());
        jScrollPane.getGutter().setBorderColor(parent.getBackground());
        return jScrollPane;
    }
}
