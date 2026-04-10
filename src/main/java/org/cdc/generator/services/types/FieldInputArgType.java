package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ioc.Inject;

import javax.swing.*;

public class FieldInputArgType extends AbstractArgType{
    @Inject int index;

    public FieldInputArgType() {
        super(1, 2);
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);

        name.getDocument().addDocumentListener(createDefaultNameDocumentListener(name::getText, () -> newJsonObject));
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        } else {
            newJsonObject.addProperty("name", "none" + index);
        }
    }

    @Override public String getName() {
        return "field_input";
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.FIELD;
    }
}
