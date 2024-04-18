package com.creatorjohn.helpers.json;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.powerups.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class PowerUpDeserializer implements JsonDeserializer<PowerUp> {

    @Override
    public PowerUp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        PowerUp.Type type = PowerUp.Type.valueOf(obj.get("type").getAsString());

        PowerUp out;

        switch (type) {
            case BOMB -> {
                JsonObject posObj = obj
                        .getAsJsonArray("positions")
                        .get(0)
                        .getAsJsonObject();
                out = new Bomb(new Position(posObj.get("x").getAsInt(), posObj.get("y").getAsInt()));
            }
            case BOMBER -> {
                int position = obj.get("position").getAsInt();
                Bomber.Direction direction = Bomber.Direction.valueOf(obj.get("direction").getAsString());
                out = new Bomber(position, direction);
            }
            case FARM -> {
                List<Position> positions = obj
                        .getAsJsonArray("positions")
                        .asList()
                        .stream()
                        .map(el -> {
                            JsonObject elObj = el.getAsJsonObject();
                            return new Position(elObj.get("x").getAsInt(), elObj.get("y").getAsInt());
                        })
                        .toList();
                out = new Farm(positions);
            }
            case RADAR -> {
                JsonObject posObj = obj
                        .getAsJsonArray("positions")
                        .get(0)
                        .getAsJsonObject();
                out = new Radar(new Position(posObj.get("x").getAsInt(), posObj.get("y").getAsInt()));
            }
            default -> out = null;
        }

        return out;
    }
}
