package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.ui.elements.PluginProceduresModElementGUI;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.VariableType;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;
import java.util.Objects;

public class InputValueArgType extends AbstractArgType {
    @InjectField PluginProceduresModElementGUI modElementGui;
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
        addConfiguration("name", name);
        var check = new VComboBox<String>();
        check.setEditable(true);
        for (String supportedType : Utils.getAllSupportedVariableTypes().stream().map(VariableType::blocklyTypeName).sorted().toList()) {
            check.addItem(supportedType);
        }
        if (newJsonObject.has("check")) {
            var elemt = newJsonObject.get("check");
            if (elemt.isJsonArray()){
                check.setSelectedItem(elemt.getAsJsonArray().get(0).getAsString());
            } else {
                check.setSelectedItem(elemt.getAsString());
            }
        }
        addConfiguration("check", check);

        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText, () -> newJsonObject));
        check.addItemListener(a -> {
            if (newJsonObject.has("check")) {
                var elemt = newJsonObject.get("check");
                if (elemt.isJsonArray()) {
                    elemt.getAsJsonArray().set(0, new JsonPrimitive(Objects.requireNonNull(check.getSelectedItem())));
                    return;
                }
            }
            newJsonObject.addProperty("check", check.getSelectedItem());
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
