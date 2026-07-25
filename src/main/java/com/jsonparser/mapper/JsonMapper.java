package com.jsonparser.mapper;

import com.jsonparser.core.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class JsonMapper {

    public <T> T readValue(String json, Class<T> clazz) {
        Parser parser = new Parser(json);
        JsonValue root = parser.parse();
        return mapValue(root, clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> T mapValue(JsonValue value, Class<T> clazz) {
        if (value == null || value instanceof JsonNull) {
            return null;
        }

        if (clazz == String.class) {
            if (value instanceof JsonString) return (T) ((JsonString) value).getValue();
            return (T) value.toString();
        }

        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(((JsonNumber) value).intValue());
        }

        if (clazz == Double.class || clazz == double.class) {
            return (T) Double.valueOf(((JsonNumber) value).doubleValue());
        }

        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(((JsonBoolean) value).getValue());
        }

        if (value instanceof JsonObject) {
            return mapObject((JsonObject) value, clazz);
        }

        throw new RuntimeException("Unsupported mapping to " + clazz.getName());
    }

    private <T> T mapObject(JsonObject obj, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }

                String jsonKey = field.getName();
                if (field.isAnnotationPresent(JsonName.class)) {
                    jsonKey = field.getAnnotation(JsonName.class).value();
                }

                if (obj.containsKey(jsonKey)) {
                    JsonValue jsonValue = obj.get(jsonKey);
                    field.setAccessible(true);

                    if (List.class.isAssignableFrom(field.getType()) && jsonValue instanceof JsonArray) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        Class<?> listClass = (Class<?>) genericType.getActualTypeArguments()[0];
                        field.set(instance, mapList((JsonArray) jsonValue, listClass));
                    } else {
                        Object mappedValue = mapValue(jsonValue, field.getType());
                        field.set(instance, mappedValue);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JSON to object", e);
        }
    }

    private <T> List<T> mapList(JsonArray arr, Class<T> itemClass) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(mapValue(arr.get(i), itemClass));
        }
        return list;
    }
}
