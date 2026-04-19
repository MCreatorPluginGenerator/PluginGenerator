package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.Utils;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;

public class FieldDataListSelectorArgType extends AbstractArgType {

    @InjectField int index;

    public FieldDataListSelectorArgType() {
        super(4, 2);
    }

    @Override public String getName() {
        return "field_data_list_selector";
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);

        var datalist = new VComboBox<String>();
        datalist.setEditable(true);
        if (jsonObject.has("datalist")) {
            datalist.setSelectedItem(jsonObject.get("datalist").getAsString());
        }
        ComboBoxUtil.updateComboBoxContents(datalist, Utils.getAllDatalistName(true));
        datalist.setSelectedItem("");
        addConfiguration("datalist", datalist);

        var testValue = new VTextField();
        if (jsonObject.has("testValue")) {
            testValue.setText(jsonObject.get("testValue").getAsString());
        }
        addConfiguration("testvalue", testValue);

        var customEntryProviders = new VComboBox<String>();
        customEntryProviders.setEditable(true);
        customEntryProviders.setSelectedItem("");
        if (jsonObject.has("customEntryProviders")) {
            customEntryProviders.setSelectedItem(jsonObject.get("customEntryProviders").getAsString());
        }
        for (ModElementType<?> allModElementType : ModElementTypeLoader.getAllModElementTypes()) {
            customEntryProviders.addItem(allModElementType.getRegistryName());
        }
        addConfiguration("custom_entry_providers", customEntryProviders);

        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText, () -> newJsonObject));
        datalist.addItemListener(a -> newJsonObject.addProperty("datalist", datalist.getSelectedItem()));
        testValue.getDocument().addDocumentListener(
                createDefaultDocumentListener("testValue", testValue::getText, () -> newJsonObject));
        customEntryProviders.addItemListener(a->{
            if (customEntryProviders.getSelectedItem() == null || customEntryProviders.getSelectedItem()
                    .isBlank()) {
                newJsonObject.remove("customEntryProviders");
            } else {
                newJsonObject.addProperty("customEntryProviders", customEntryProviders.getSelectedItem());
            }
        });
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        ifHasNameThenPut(jsonObject, newJsonObject, index);
        if (jsonObject.has("datalist")) {
            newJsonObject.addProperty("datalist", jsonObject.get("datalist").getAsString());
        }
        if (jsonObject.has("testValue")) {
            newJsonObject.addProperty("testValue", jsonObject.get("testValue").getAsString());
        }
        if (jsonObject.has("customEntryProviders")) {
            newJsonObject.addProperty("customEntryProviders", jsonObject.get("customEntryProviders").getAsString());
        }
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.FIELD;
    }
}
