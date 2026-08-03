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
import java.util.function.Supplier;

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
        modifyToolBar(mCreator, toolbar, propertiesTextArea, result,null);
        panel.add(toolbar, "North");
        return panel;
    }

    default void modifyToolBar(MCreator mCreator, JToolBar toolbar, JTextArea propertiesTextArea, JTextArea result,
            Supplier<MCreator> debugMCreator) {
        var generate = new JButton(UIRES.get("16px.build"));
        generate.setToolTipText("Generate");
        toolbar.add(generate);

        generate.addActionListener(_ -> {
            result.setText("");
            var templateGenerator = getTemplateGenerator();
            if (templateGenerator != null) {
                if (propertiesTextArea.getText().isBlank()) {
                    var writer = new StringWriter();
                    try {
                        var prop = getDefaultParametersProperties();
                        if (prop != null)
                            prop.store(writer, "Edit the value to change the result type indicator: /*@type*/");
                    } catch (IOException ignored) {
                    }
                    propertiesTextArea.setText(writer.toString().replace(System.lineSeparator(),"\n"));
                }
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
                var pro = getDefaultParameterMap();
                if (pro == null){
                    return;
                }
                map.putAll(pro);

                try {
                    var str = templateGenerator.generateFromString(getFreemakerContent(properties), map);
                    var clean = new CodeCleanup();
                    if (debugMCreator != null) {
                        result.setText(
                                clean.reformatTheCodeAndOrganiseImports(debugMCreator.get().getWorkspace(), str));
                    } else {
                        result.setText(clean.reformatTheCodeOnly(str));
                    }
                } catch (TemplateGeneratorException e) {
                    var writer1 = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer1));
                    mCreator.getGradleConsole().append(writer1.toString());
                    mCreator.getGradleConsole().append(map.toString());
                    result.setText("Error: A exception was thrown, please read the console.");
                }
            } else {
                result.setText("Error: " + L10N.t("warnings.should_open_a_selected_generator_workspace"));
            }
        });
    }

    TemplateGenerator getTemplateGenerator();

    String getFreemakerContent(Properties properties);

    /**
     * This will be output to the propertiesTextArea which is editable for user.
     */
    Properties getDefaultParametersProperties();

    Map<String, Object> getDefaultParameterMap();

    JScrollPane getResultArea();
}
