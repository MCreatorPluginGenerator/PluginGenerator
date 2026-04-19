package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;

public class FieldAIConditionSelectorArgType extends AbstractArgType{
    @InjectField int index;

    private static InputDummyArgType INSTANCE;

    public FieldAIConditionSelectorArgType() {
        super(1, 2);
    }

    public static InputDummyArgType getInstance() {
        if (INSTANCE == null)
            INSTANCE = new InputDummyArgType();
        return INSTANCE;
    }

    @Override public String getName() {
        return "field_ai_condition_selector";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);
        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);

        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText, () -> newJsonObject));
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        ifHasNameThenPut(jsonObject, newJsonObject, index);
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.FIELD;
    }
}
