package org.cdc.generator.services.types;

import com.formdev.flatlaf.json.Json;
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
        configurationPanel = new JPanel(new GridLayout(row, col));
        initNewJsonObject(jsonObject, newJsonObject);
        return configurationPanel;
    }

    public JPanel wrapConfigurationPanel(){
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

    protected DocumentListener createDefaultNameDocumentListener(Supplier<String> name,Supplier<JsonObject> newJsonObject){
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent documentEvent) {
                if (name.get().isBlank()){
                    newJsonObject.get().remove("name");
                } else {
                    newJsonObject.get().addProperty("name", name.get());
                }
            }

            @Override public void removeUpdate(DocumentEvent documentEvent) {
                if (name.get().isBlank()){
                    newJsonObject.get().remove("name");
                } else {
                    newJsonObject.get().addProperty("name", name.get());
                }
            }

            @Override public void changedUpdate(DocumentEvent documentEvent) {
                if (name.get().isBlank()){
                    newJsonObject.get().remove("name");
                } else {
                    newJsonObject.get().addProperty("name", name.get());
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
