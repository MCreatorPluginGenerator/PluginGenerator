package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;

import javax.swing.*;

public class InputDummyArgType extends AbstractArgType {
    private static InputDummyArgType INSTANCE;

    public InputDummyArgType() {
        super(1, 2);
    }

    public static InputDummyArgType getInstance() {
        if (INSTANCE == null)
            INSTANCE = new InputDummyArgType();
        return INSTANCE;
    }

    @Override public String getName() {
        return "input_dummy";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);
        var name = new VTextField();
        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText,()->newJsonObject));
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject,JsonObject newJsonObject) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        }
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.DEPENDENCY;
    }
}
