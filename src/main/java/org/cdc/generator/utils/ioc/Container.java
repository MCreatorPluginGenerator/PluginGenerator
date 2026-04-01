package org.cdc.generator.utils.ioc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Container {
    public static final Logger LOG = LogManager.getLogger(Container.class);

    private static Container instance;

    public static Container getInstance() {
        if (instance == null) {
            instance = new Container();
        }
        return instance;
    }

    private final Map<String, Supplier<Object>> objectMap = new ConcurrentHashMap<>();
    private final Map<String, Supplier<Object>> tempObjectMap = new ConcurrentHashMap<>();

    private Container() {
        objectMap.put("container", () -> {
            var container = new Container();
            container.objectMap.putAll(objectMap);
            return container;
        });
    }

    public void registerObject(String name, Supplier<Object> objectSupplier) {
        objectMap.put(name, objectSupplier);
    }

    public void registerTemporaryObject(String name, Supplier<Object> objectSupplier) {
        tempObjectMap.put(name, objectSupplier);
    }

    public <V> V inject(V object) {
        var cls = object.getClass();

        var map = new HashMap<String, Supplier<Object>>();
        map.putAll(objectMap);
        map.putAll(tempObjectMap);
        map.put("LOGGER", () -> LogManager.getLogger(cls));
        map.put("LOG", () -> LogManager.getLogger(cls));

        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class) && map.containsKey(field.getName())) {
                field.setAccessible(true);
                var value = map.get(field.getName()).get();
                if (field.getType().isAssignableFrom(value.getClass())) {
                    try {
                        field.set(object, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return object;
    }

    public void endTemporaryLife() {
        tempObjectMap.clear();
    }
}
