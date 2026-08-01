package org.cdc.generator.ui.elements;

import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.UIRES;

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
    default JComponent getDebuggerComponent(MCreator mCreator){
        var panel = new JPanel(new BorderLayout());

        var propertiesTextArea = new JTextArea();
        propertiesTextArea.setRows(10);
        propertiesTextArea.setBorder(BorderFactory.createTitledBorder("Format: Properties"));
        panel.add(new JScrollPane(propertiesTextArea),BorderLayout.SOUTH);

        var result = getResultArea();
        result.setBorder(BorderFactory.createTitledBorder("Result"));
        result.setEditable(false);
        panel.add(new JScrollPane(result),"Center");

        var toolbar = new JToolBar();
        toolbar.setBorder(BorderFactory.createTitledBorder("Control"));

        var generate = new JButton(UIRES.get("16px.build"));
        generate.setToolTipText("Generate");
        toolbar.add(generate);

        generate.addActionListener(_->{
            if (propertiesTextArea.getText().isBlank()){
                var writer = new StringWriter();
                try {
                    getDefaultParametersProperties().store(writer,"");
                } catch (IOException ignored) {
                }
                propertiesTextArea.setText(writer.toString());
            }
            var templateGenerator = mCreator.getGenerator().getTemplateGeneratorFromName("debugger");
            if (templateGenerator != null){
                var map = new HashMap<String,Object>();
                var properties = new Properties();
                try {
                    properties.load(new StringReader(propertiesTextArea.getText()));
                } catch (IOException ignored) {

                }
                for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                    map.put(objectObjectEntry.getKey().toString(),objectObjectEntry.getValue());
                }
                map.putAll(getDefaultParameterMap());

                try {
                    var str = templateGenerator.generateFromString(getFreemakerContent(),map);
                    result.setText(str);
                } catch (TemplateGeneratorException e) {
                    var writer1 = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer1));
                    mCreator.getGradleConsole().append(writer1.toString());
                    mCreator.getGradleConsole().append(map.toString());
                }
            }
        });
        panel.add(toolbar,"North");

        return panel;
    }

    String getFreemakerContent();

    /**
     * This will be output to the propertiesTextArea which is editable for user.
     */
    Properties getDefaultParametersProperties();

    Map<String,Object> getDefaultParameterMap();

    JTextArea getResultArea();
}
