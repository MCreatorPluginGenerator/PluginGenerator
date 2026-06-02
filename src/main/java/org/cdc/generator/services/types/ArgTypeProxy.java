package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import org.cdc.generator.utils.interfaces.IArg0Type;

public class ArgTypeProxy implements Cloneable{


    public static IArg0Type getArg0Type(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();
        IArg0Type type1 = null;
        CustomArgType customArgType = null;
        for (IArg0Type arg0type : IArg0Type.arg0types) {
            if (arg0type instanceof CustomArgType){
                customArgType = (CustomArgType) arg0type;
            }
            if (arg0type.getName().equals(type)) {
                type1 = arg0type;
                break;
            }
        }
        if (type1 == null){
            return customArgType;
        }
        return type1;
    }

    private JsonObject arg0Json;

    public ArgTypeProxy(JsonObject arg0Json) {
        this.arg0Json = arg0Json;
    }

    public String getUniqueName() {
        return getArg0Type().getUniqueName(arg0Json);
    }

    public JsonObject getArg0Json() {
        return arg0Json;
    }

    public void setArg0Json(JsonObject arg0Json) {
        this.arg0Json = arg0Json;
    }

    public IArg0Type getArg0Type() {
        return getArg0Type(arg0Json);
    }

    public String getArg0TypeName() {return getArg0Type().getName();}

    @Override public String toString() {
        return getUniqueName();
    }

    @Override public ArgTypeProxy clone() {
        try {
            ArgTypeProxy clone = (ArgTypeProxy) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
