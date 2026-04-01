package org.cdc.generator.utils;

import net.mcreator.ui.component.util.PanelUtils;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.stream.Collectors;

public class DialogUtils {
    public static int showOptionPaneWithTextArea(RSyntaxTextArea jTextArea, Component parent, String title,
            Collection<?> collections) {
        RTextScrollPane jScrollPane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(jTextArea, parent);
        jScrollPane.setBorder(BorderFactory.createTitledBorder("Lines"));
        if (!collections.isEmpty()) {
            jTextArea.setText(collections.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }
        return JOptionPane.showConfirmDialog(parent, jScrollPane, title, JOptionPane.YES_NO_OPTION);
    }

    public static int showOptionPaneWithTextAreaAndToolBar(RSyntaxTextArea jTextArea, JToolBar toolbar,
            Component parent, String title, Collection<?> collections) {
        RTextScrollPane jScrollPane = RSyntaxTextAreaFactory.createDefaultTextScrollPane(jTextArea, parent);
        jScrollPane.setBorder(BorderFactory.createTitledBorder("Lines"));
        if (!collections.isEmpty()) {
            jTextArea.setText(collections.stream().map(Object::toString).collect(Collectors.joining("\n")));
        }
        return JOptionPane.showConfirmDialog(parent, PanelUtils.northAndCenterElement(toolbar, jScrollPane), title,
                JOptionPane.YES_NO_OPTION);
    }

}
