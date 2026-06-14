package org.cdc.generator.ui.elements;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;
import org.cdc.generator.elements.MovableCustomResourceModElement;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;
import org.cdc.generator.utils.validators.NotEmptyValidator;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/6/14
 */
public class MovableCustomResourceModElementGUI
        extends AbstractConfigurationTableModElementGUI<MovableCustomResourceModElement> {

    private VTextField folder;
    private RSyntaxTextArea content;

    public MovableCustomResourceModElementGUI(MCreator mcreator, @NonNull ModElement modElement, boolean editingMode) {
        super(mcreator, modElement, editingMode, null);

        folder = new VTextField();
        content = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();

        if (editingMode) {
            folder.setEnabled(false);
        }

        initGUI();
        finalizeGUI();
    }

    @Override protected void initGUI() {
        folder.setValidator(new NotEmptyValidator(folder::getText));
        addConfigurationWithHelpEntry("folder", folder);

        content.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                var file = folder.getText();
                if (file.endsWith(".json") || file.endsWith(".mcmeta")) {
                    content.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
                } else if (file.endsWith(".yaml") || file.endsWith(".yml")) {
                    content.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_YAML);
                } else {
                    content.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JAVA);
                }
            }
        });

        addPage("Configuration", PanelUtils.northAndCenterElement(buildConfiguration(2),
                RSyntaxTextAreaFactory.createDefaultTextScrollPane(content, this))).validate(folder);
    }

    @Override protected void openInEditingMode(MovableCustomResourceModElement generatableElement) {
        this.folder.setText(generatableElement.folder);
        this.content.setText(generatableElement.content);
    }

    @Override public MovableCustomResourceModElement getElementFromGUI() {
        var element = new MovableCustomResourceModElement(modElement);
        element.folder = folder.getText().replace('\\','/');
        element.content = content.getText();
        return element;
    }

    @Override public @Nullable URI contextURL() throws URISyntaxException {
        return null;
    }
}
