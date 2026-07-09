package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import org.cdc.generator.utils.Arg0InputType;
import org.cdc.generator.utils.ioc.InjectField;

import javax.swing.*;

public class FieldImageArgType extends AbstractArgType {
    @InjectField int index;

    public FieldImageArgType() {
        super(3, 2);
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        super.getEditor(jsonObject, newJsonObject);

        var src = new VComboBox<String>();
        UIRES.preloadImages();
        src.addItem("./res/server.png");
        src.addItem("./res/client.png");
        src.addItem("./res/null.png");
        src.setEditable(true);
        if (jsonObject.has("src")) {
            src.setSelectedItem(jsonObject.get("src").getAsString());
        }
        addConfiguration("src", src);

        var widthModel = new SpinnerNumberModel();
        widthModel.setMinimum(0);
        var width = new JSpinner(widthModel);
        if (jsonObject.has("width")) {
            widthModel.setValue(jsonObject.get("width").getAsInt());
        }
        addConfiguration("width", width);

        var heightModel = new SpinnerNumberModel();
        heightModel.setMinimum(0);
        var height = new JSpinner(heightModel);
        if (jsonObject.has("height")) {
            heightModel.setValue(jsonObject.get("height").getAsInt());
        }
        addConfiguration("height", height);

        src.addItemListener(a -> {
            newJsonObject.addProperty("src", src.getSelectedItem());
        });
        width.addChangeListener(a -> {
            if (widthModel.getNumber().equals(widthModel.getMinimum())) {
                newJsonObject.remove("width");
            } else {
                newJsonObject.addProperty("width", widthModel.getNumber());
            }
        });
        height.addChangeListener(a -> {
            if (heightModel.getNumber().equals(heightModel.getMinimum())) {
                newJsonObject.remove("height");
            } else {
                newJsonObject.addProperty("height", heightModel.getNumber());
            }
        });
        return wrapConfigurationPanel();
    }

    @Override protected void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject) {
        if (jsonObject.has("src")) {
            newJsonObject.addProperty("src", jsonObject.get("src").getAsString());
        } else {
            newJsonObject.addProperty("src", "./res/null.png");
        }
        if (jsonObject.has("width")) {
            newJsonObject.addProperty("width", jsonObject.get("width").getAsInt());
        }
        if (jsonObject.has("height")) {
            newJsonObject.addProperty("height", jsonObject.get("height").getAsInt());
        }
    }

    @Override public String getName() {
        return "field_image";
    }

    @Override public Arg0InputType getType() {
        return Arg0InputType.DEPENDENCY;
    }

    @Override public String getUniqueName(JsonObject jsonObject) {
        return jsonObject.get("src").getAsString();
    }
}
