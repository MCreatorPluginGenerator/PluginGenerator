package org.cdc.generator.services.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.ui.TypeListField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.VariableType;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;
import java.util.List;

public class InputValueArgType extends AbstractArgType {
    @InjectField MCreator mcreator;
    @InjectField int index;

    public InputValueArgType() {
        super(3, 2);
    }

    @Override public String getName() {
        return "input_value";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        if (newJsonObject.has("name")) {
            name.setText(newJsonObject.get("name").getAsString());
        }
        name.setPreferredSize(Utils.tryToGetTextFieldSize());
        addConfiguration("name", name);

        var check = new TypeListField(mcreator, VariableType::blocklyTypeName);
        if (newJsonObject.has("check")) {
            var elemt = newJsonObject.get("check");
            if (elemt.isJsonArray()) {
                check.setListElements(elemt.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList());
            } else {
                check.setListElements(List.of(elemt.getAsString()));
            }
        }
        addConfiguration("check", check);

        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText, () -> newJsonObject));
        check.addChangeListener(a -> {
            var array = new JsonArray();
            for (String listElement : check.getListElements()) {
                array.add(listElement);
            }
            newJsonObject.add("check", array);
        });

        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        } else {
            newJsonObject.addProperty("name", "none" + index);
        }
        if (jsonObject.has("check")) {
            newJsonObject.add("check", jsonObject.get("check"));
        } else {
            newJsonObject.addProperty("check", VariableTypeLoader.BuiltInTypes.NUMBER.getBlocklyVariableType());
        }
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.INPUT;
    }
}
