package org.cdc.generator.services.types;

import com.google.gson.JsonObject;
import org.cdc.generator.utils.interfaces.IArg0Type;

public class ArgTypeProxy {
    public static ArgTypeProxy createArgTypeProxy(JsonObject jsonObject){
        return new ArgTypeProxy(jsonObject);
    }

    public static IArg0Type getArg0Type(JsonObject jsonObject){
        String type = jsonObject.get("type").getAsString();
        IArg0Type type1 = null;
        for (IArg0Type arg0type : IArg0Type.arg0types) {
            if (arg0type.getName().equals(type)){
                type1 = arg0type;
                break;
            }
        }
        return type1;
    }

    private JsonObject arg0Json;

    public ArgTypeProxy(JsonObject arg0Json) {
        this.arg0Json = arg0Json;
    }

    public String getUniqueName(){
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

    @Override public String toString() {
        return getUniqueName();
    }
}
