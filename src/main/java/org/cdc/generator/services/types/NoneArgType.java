package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NoneArgType extends AbstractArgType {
    private static NoneArgType INSTANCE;

    public NoneArgType() {
        super(1, 2);
    }

    public static NoneArgType getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NoneArgType();
        return INSTANCE;
    }

    @Override public String getName() {
        return "input_dummy";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        JPanel configurationPanel = super.getEditor(jsonObject, newJsonObject);
        var name = new VTextField();
        name.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent documentEvent) {
                if (name.getText().isBlank()){
                    newJsonObject.remove("name");
                } else {
                    newJsonObject.addProperty("name", name.getText());
                }
            }

            @Override public void removeUpdate(DocumentEvent documentEvent) {
                if (name.getText().isBlank()){
                    newJsonObject.remove("name");
                } else {
                    newJsonObject.addProperty("name", name.getText());
                }
            }

            @Override public void changedUpdate(DocumentEvent documentEvent) {
                if (name.getText().isBlank()){
                    newJsonObject.remove("name");
                } else {
                    newJsonObject.addProperty("name", name.getText());
                }
            }
        });
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);
        return PanelUtils.totalCenterInPanel(configurationPanel);
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject,JsonObject newJsonObject) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        }
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.DEPENDENCY;
    }

    @Override public String getUniqueName(JsonObject jsonObject) {
        return "none";
    }
}
