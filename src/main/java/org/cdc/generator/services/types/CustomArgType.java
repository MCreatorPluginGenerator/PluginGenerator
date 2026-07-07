package org.cdc.generator.services.types;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.mcreator.generator.io.JSONWriter;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.interfaces.IArg0Type;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;

public class CustomArgType extends JPanel implements IArg0Type {

    @InjectField int index;

    private JTextArea textArea;

    private FocusListener focusListener;

    public CustomArgType() {
        this.setLayout(new BorderLayout());

        textArea = new JTextArea();
        this.add("Center", textArea);
    }

    @Override public String getName() {
        return "custom_type";
    }

    @Override public Component getEditor(JsonObject oldJsonObject, JsonObject newJsonObject) {
        if (focusListener != null) {
            textArea.removeFocusListener(focusListener);
        }
        textArea.setText(JSONWriter.formatJSON(oldJsonObject.toString()));
        focusListener = new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                newJsonObject.asMap().clear();
                var custom = new Gson().fromJson(textArea.getText(), JsonObject.class);
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : custom.entrySet()) {
                    newJsonObject.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
                }
            }
        };
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : oldJsonObject.entrySet()) {
            newJsonObject.add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
        }
        textArea.addFocusListener(focusListener);
        return this;
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.DEPENDENCY;
    }

    @Override public String getUniqueName(JsonObject jsonObject) {
        if (jsonObject.has("name")) {
            return jsonObject.get("name").getAsString();
        }
        return jsonObject.entrySet().stream().map(Map.Entry::getValue).findFirst()
                .orElse(new JsonPrimitive("none" + index)).getAsString();
    }
}
