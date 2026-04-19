package org.cdc.generator.services.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.component.VTextField;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class FieldDropdownArgType extends AbstractArgType {
    @InjectField int index;

    public FieldDropdownArgType() {
        super(1, 2);
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        var configuration = super.getEditor(jsonObject, newJsonObject);

        var name = new VTextField();
        if (jsonObject.has("name")) {
            name.setText(jsonObject.get("name").getAsString());
        }
        addConfiguration("name", name);

        var options = new JTextArea();
        options.setColumns(20);
        options.setBorder(BorderFactory.createTitledBorder("Options(Format: properties):"));
        if (jsonObject.has("options")) {
            var properties = new Properties();
            for (JsonElement jsonElement : jsonObject.get("options").getAsJsonArray()) {
                if (jsonElement instanceof JsonArray jsonArray) {
                    properties.setProperty(jsonArray.get(0).getAsString(), jsonArray.get(1).getAsString());
                }
            }
            options.setText(properties.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n")));
        }

        name.getDocument().addDocumentListener(createDefaultDocumentListener(name::getText, () -> newJsonObject));
        options.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                var pro = new Properties();
                try {
                    pro.load(new StringReader(options.getText()));
                    var jsonaaa = new JsonArray();
                    for (Map.Entry<Object, Object> objectObjectEntry : pro.entrySet()) {
                        var js = new JsonArray(2);
                        js.add(objectObjectEntry.getKey().toString());
                        js.add(objectObjectEntry.getValue().toString());
                        jsonaaa.add(js);
                    }
                    newJsonObject.add("options", jsonaaa);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(configuration, options));
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        ifHasNameThenPut(jsonObject, newJsonObject, index);
        if (jsonObject.has("options")) {
            newJsonObject.add("options", jsonObject.get("options"));
        }
    }

    @Override public String getName() {
        return "field_dropdown";
    }

    @Override public Arg0InputType getType() {
        return null;
    }
}
