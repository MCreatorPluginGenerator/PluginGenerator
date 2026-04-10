package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ioc.Inject;

import javax.swing.*;

public class FieldCheckBoxArgType extends AbstractArgType {

    @Inject int index;

    public FieldCheckBoxArgType() {
        super(2, 2);
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);
        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);
        var checked = createDefaultCheckBox();
        if (jsonObject.has("checked")) {
            checked.setSelected(jsonObject.get("checked").getAsBoolean());
        }
        addConfiguration("checked", checked);

        name.getDocument().addDocumentListener(createDefaultNameDocumentListener(name::getText, () -> newJsonObject));
        checked.addChangeListener(a -> {
            newJsonObject.addProperty("checked", checked.isSelected());
        });
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        } else {
            newJsonObject.addProperty("name", "none" + index);
        }
        if (jsonObject.has("checked")) {
            newJsonObject.addProperty("checked", jsonObject.get("checked").getAsBoolean());
        } else {
            newJsonObject.addProperty("checked", false);
        }
    }

    @Override public String getName() {
        return "field_checkbox";
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.FIELD;
    }
}
