package org.cdc.generator.ui.elements;

import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.java.CodeCleanup;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import org.cdc.generator.utils.factories.RSyntaxTextAreaFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface IFreemakerDebugger {
    default JComponent getDebuggerComponent(MCreator mCreator) {
        var panel = new JPanel(new BorderLayout());

        var propertiesTextArea = RSyntaxTextAreaFactory.createDefaultRSyntaxTextArea();
        propertiesTextArea.setRows(10);
        propertiesTextArea.setSyntaxEditingStyle("text/properties");
        propertiesTextArea.setBorder(BorderFactory.createTitledBorder("Format: Properties"));
        panel.add(RSyntaxTextAreaFactory.createDefaultTextScrollPane(propertiesTextArea, mCreator), BorderLayout.SOUTH);

        var result1 = getResultArea();
        JTextArea result = (JTextArea) result1.getViewport().getView();
        result1.setBorder(BorderFactory.createTitledBorder("Result"));
        panel.add(result1, "Center");

        var toolbar = new JToolBar();
        toolbar.setBorder(BorderFactory.createTitledBorder("Control"));
        modifyToolBar(mCreator, toolbar, propertiesTextArea, result);
        panel.add(toolbar, "North");
        return panel;
    }

    default void modifyToolBar(MCreator mCreator, JToolBar toolbar, JTextArea propertiesTextArea, JTextArea result) {
        var generate = new JButton(UIRES.get("16px.build"));
        generate.setToolTipText("Generate");
        toolbar.add(generate);

        generate.addActionListener(_ -> {
            if (propertiesTextArea.getText().isBlank()) {
                var writer = new StringWriter();
                try {
                    var prop = getDefaultParametersProperties();
                    if (prop != null)
                        prop.store(writer, "Edit the value to change the result");
                } catch (IOException ignored) {
                }
                propertiesTextArea.setText(writer.toString());
            }
            var templateGenerator = getTemplateGenerator();
            if (templateGenerator != null) {
                var map = new HashMap<String, Object>();
                var properties = new Properties();
                try {
                    properties.load(new StringReader(propertiesTextArea.getText()));
                } catch (IOException ignored) {

                }
                for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                    var key = objectObjectEntry.getKey().toString();
                    if (!key.startsWith("debugger.")) {
                        map.put(key, objectObjectEntry.getValue());
                    }
                }
                map.putAll(getDefaultParameterMap());

                try {
                    var str = templateGenerator.generateFromString(getFreemakerContent(), map);
                    result.setText(new CodeCleanup().reformatTheCodeAndOrganiseImports(mCreator.getWorkspace(), str));
                } catch (TemplateGeneratorException e) {
                    var writer1 = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer1));
                    mCreator.getGradleConsole().append(writer1.toString());
                    mCreator.getGradleConsole().append(map.toString());
                }
            } else {
                result.setText("Error: " + L10N.t("warnings.should_open_a_selected_generator_workspace"));
            }
        });
    }

    TemplateGenerator getTemplateGenerator();

    String getFreemakerContent();

    /**
     * This will be output to the propertiesTextArea which is editable for user.
     */
    Properties getDefaultParametersProperties();

    Map<String, Object> getDefaultParameterMap();

    JScrollPane getResultArea();
}
