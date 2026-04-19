package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;

public class FieldNumberArgType extends AbstractArgType {
    @InjectField int index;

    public FieldNumberArgType() {
        super(2, 2);
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);

        var numberModel = new SpinnerNumberModel();
        var number = new JSpinner(numberModel);
        if (jsonObject.has("text")) {
            numberModel.setValue(jsonObject.get("text").getAsString());
        }
        addConfiguration("value", number);

        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText, () -> newJsonObject));
        number.addChangeListener(a -> newJsonObject.addProperty("value", numberModel.getNumber()));
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        ifHasNameThenPut(jsonObject, newJsonObject, index);

        if (jsonObject.has("value")){
            newJsonObject.addProperty("value",jsonObject.get("value").getAsNumber());
        }
    }

    @Override public String getName() {
        return "field_input";
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.FIELD;
    }
}
