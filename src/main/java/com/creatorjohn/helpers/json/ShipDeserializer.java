package com.creatorjohn.helpers.json;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class ShipDeserializer implements JsonDeserializer<Ship> {

    @Override
    public Ship deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (JConfig.isNotObject(json)) return null;

        JsonObject obj = json.getAsJsonObject();

        if (JConfig.isNotPrimitive(obj.get("size"))) return null;
        else if (JConfig.isNotArray(obj.get("positions"))) return null;

        int size = obj.get("size").getAsInt();
        List<Position> positions = JConfig.convertArray("positions", obj, Position.class);
        return new Ship(size, positions);
    }
}
