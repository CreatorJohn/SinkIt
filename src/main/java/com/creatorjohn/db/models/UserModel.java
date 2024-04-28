package com.creatorjohn.db.models;

import com.creatorjohn.helpers.json.JConfig;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public record UserModel(String id, String username, String password, UserStats stats) implements DataModel<UserModel> {

    public UserModel(String username, String password) {
        this(UUID.randomUUID().toString(), username, password, new UserStats());
    }

    @Override
    public UserModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (JConfig.isNotObject(json)) return null;

        JsonObject obj = json.getAsJsonObject();

        if (JConfig.isNotPrimitive(obj.get("id"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("username"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("password"))) return null;
        else if (!obj.has("stats")) return null;

        String id = obj.get("id").getAsString();
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();
        UserStats stats = MyGson.instance.fromJson(obj.get("stats").getAsString(), UserStats.class);

        return new UserModel(id, username, password, stats);
    }

    @Override
    public Map.Entry<String, UserModel> toMapEntry() {
        return Map.entry(id(), this);
    }

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }
}
