package org.cdc.generator.utils.factories;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;

public class AutoCompletionFactory {
    public static void createDefaultCompletion(RSyntaxTextArea rSyntaxTextArea,
            Supplier<CompletionProvider> providerSupplier) {
        AutoCompletion autoCompletion = new AutoCompletion(providerSupplier.get());
        autoCompletion.install(rSyntaxTextArea);
        autoCompletion.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
    }
}
