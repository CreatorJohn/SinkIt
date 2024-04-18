package com.creatorjohn.helpers.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Objects;

public class JConfig {
    public static boolean isNotPrimitive(JsonElement elem) {
        return !elem.isJsonPrimitive();
    }

    public static boolean isNotObject(JsonElement elem) {
        return !elem.isJsonObject();
    }

    public static boolean isNotArray(JsonElement elem) {
        return !elem.isJsonArray();
    }

    public static <T> List<T> convertArray(String key, JsonObject obj, Class<T> iClass) {
        return obj
                .getAsJsonArray(key)
                .asList()
                .stream()
                .map(it -> MyGson.instance.fromJson(it.toString(), iClass))
                .filter(Objects::nonNull)
                .toList();
    }
}
