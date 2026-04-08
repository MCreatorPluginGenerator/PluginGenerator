package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.VariableType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InputValueArgType extends AbstractArgType {
    public InputValueArgType() {
        super(3, 2);
    }

    @Override public String getName() {
        return "input_value";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        JPanel configurationPanel = super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        name.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent documentEvent) {
                newJsonObject.addProperty("name", name.getText());
            }

            @Override public void removeUpdate(DocumentEvent documentEvent) {
                newJsonObject.addProperty("name", name.getText());
            }

            @Override public void changedUpdate(DocumentEvent documentEvent) {
                newJsonObject.addProperty("name", name.getText());
            }
        });
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);

        var check = new VComboBox<String>();
        for (VariableType supportedType : Utils.getAllSupportedVariableTypes()) {
            check.addItem(supportedType.blocklyTypeName());
        }
        check.addItemListener(a -> {
            newJsonObject.addProperty("check", check.getSelectedItem());
        });
        if (jsonObject.has("check")) {
            check.setSelectedItem(jsonObject.get("check").getAsString());
        }
        addConfiguration("check", check);
        return PanelUtils.totalCenterInPanel(configurationPanel);
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        } else {
            jsonObject.addProperty("name", "none");
        }
        if (jsonObject.has("check")) {
            newJsonObject.addProperty("check", jsonObject.get("check").getAsString());
        } else {
            newJsonObject.addProperty("check", "");
        }
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.INPUT;
    }

    @Override public String getUniqueName(JsonObject jsonObject) {
        var json = jsonObject.get("name");
        if (json == null) {
            return "none";
        }
        return json.getAsString();
    }
}
