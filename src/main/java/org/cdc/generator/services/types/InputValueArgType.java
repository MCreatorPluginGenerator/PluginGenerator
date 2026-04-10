package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.cdc.generator.ui.elements.PluginProceduresElementGUI;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.VariableType;
import org.cdc.generator.utils.ioc.Inject;

import javax.swing.*;

public class InputValueArgType extends AbstractArgType {
    @Inject PluginProceduresElementGUI modElementGui;
    @Inject int index;

    public InputValueArgType() {
        super(3, 2);
    }

    @Override public String getName() {
        return "input_value";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);
        var check = new VComboBox<String>();
        for (VariableType supportedType : Utils.getAllSupportedVariableTypes()) {
            check.addItem(supportedType.blocklyTypeName());
        }
        if (jsonObject.has("check")) {
            check.setSelectedItem(jsonObject.get("check").getAsString());
        }
        addConfiguration("check", check);

        name.getDocument().addDocumentListener(createDefaultNameDocumentListener(name::getText, () -> newJsonObject));
        check.addItemListener(a -> {
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
            newJsonObject.addProperty("check", jsonObject.get("check").getAsString());
        } else {
            newJsonObject.addProperty("check", VariableTypeLoader.BuiltInTypes.NUMBER.getBlocklyVariableType());
        }
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.INPUT;
    }
}
