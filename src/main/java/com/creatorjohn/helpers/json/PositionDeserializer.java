package com.creatorjohn.helpers.json;

import com.creatorjohn.helpers.Position;
import com.google.gson.*;

import java.lang.reflect.Type;

public class PositionDeserializer implements JsonDeserializer<Position> {

    @Override
    public Position deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (JConfig.isNotObject(json)) return null;

        JsonObject obj = json.getAsJsonObject();

        if (JConfig.isNotPrimitive(obj.get("x"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("y"))) return null;

        return new Position(obj.get("x").getAsInt(), obj.get("y").getAsInt());
    }
}
