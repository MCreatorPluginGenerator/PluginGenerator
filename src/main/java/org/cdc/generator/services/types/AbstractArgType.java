package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import org.cdc.generator.utils.interfaces.IArg0Type;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.function.Supplier;

public abstract class AbstractArgType implements IArg0Type {
    private final int row;
    private final int col;

    private JPanel configurationPanel;

    public AbstractArgType(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override public JPanel getEditor(JsonObject jsonObject, JsonObject newJsonObject) {
        configurationPanel = new JPanel(new GridLayout(row, col,5,5));
        initNewJsonObject(jsonObject, newJsonObject);
        return configurationPanel;
    }

    public JPanel wrapConfigurationPanel() {
        return PanelUtils.totalCenterInPanel(configurationPanel);
    }

    protected abstract void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject);

    protected void addConfiguration(String name, Component component) {
        configurationPanel.add(L10N.label("elementgui.arg0." + name));
        configurationPanel.add(component);
    }

    protected JCheckBox createDefaultCheckBox() {
        return L10N.checkbox("elementgui.common.enable");
    }

    protected DocumentListener createDefaultDocumentListener(Supplier<String> nameSupplier,
            Supplier<JsonObject> newJsonObject) {
        return createDefaultDocumentListener("name", nameSupplier, newJsonObject);
    }

    protected void ifHasNameThenPut(JsonObject jsonObject, JsonObject newJsonObject, int index) {
        if (jsonObject.has("name")) {
            newJsonObject.addProperty("name", jsonObject.get("name").getAsString());
        } else {
            newJsonObject.addProperty("name", "none" + index);
        }
    }

    protected DocumentListener createDefaultDocumentListener(String nameKey, Supplier<String> nameSupplier,
            Supplier<JsonObject> newJsonObject) {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent documentEvent) {
                if (nameSupplier.get().isBlank()) {
                    newJsonObject.get().remove(nameKey);
                } else {
                    newJsonObject.get().addProperty(nameKey, nameSupplier.get());
                }
            }

            @Override public void removeUpdate(DocumentEvent documentEvent) {
                if (nameSupplier.get().isBlank()) {
                    newJsonObject.get().remove(nameKey);
                } else {
                    newJsonObject.get().addProperty(nameKey, nameSupplier.get());
                }
            }

            @Override public void changedUpdate(DocumentEvent documentEvent) {
                if (nameSupplier.get().isBlank()) {
                    newJsonObject.get().remove(nameKey);
                } else {
                    newJsonObject.get().addProperty(nameKey, nameSupplier.get());
                }
            }
        };
    }

    @Override public String getUniqueName(JsonObject jsonObject) {
        var json = jsonObject.get("name");
        if (json == null) {
            return "none";
        }
        return json.getAsString();
    }
}
