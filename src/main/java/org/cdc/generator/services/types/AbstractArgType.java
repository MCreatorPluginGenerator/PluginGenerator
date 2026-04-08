package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import net.mcreator.ui.init.L10N;
import org.cdc.generator.utils.interfaces.IArg0Type;

import javax.swing.*;
import java.awt.*;

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

    protected abstract void initNewJsonObject(JsonObject jsonObject, JsonObject newJsonObject);

    protected void addConfiguration(String name, Component component) {
        configurationPanel.add(L10N.label("elementgui.arg0." + name));
        configurationPanel.add(component);
    }

    protected JCheckBox createDefaultCheckBox() {
        return L10N.checkbox("elementgui.common.enable");
    }
}
