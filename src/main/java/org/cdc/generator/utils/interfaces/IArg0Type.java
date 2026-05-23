package org.cdc.generator.utils.interfaces;

import com.google.gson.JsonObject;
import org.cdc.generator.PluginMain;
import org.cdc.generator.utils.Arg0InputType;

import java.awt.*;
import java.util.ServiceLoader;

public interface IArg0Type {

    ServiceLoader<IArg0Type> arg0types = ServiceLoader.load(IArg0Type.class, PluginMain.getINSTANCE()
            .getDependsClassLoader());

    String getName();

    Component getEditor(JsonObject oldJsonObject, JsonObject newJsonObject);

    Arg0InputType getType();

    String getUniqueName(JsonObject jsonObject);
}
