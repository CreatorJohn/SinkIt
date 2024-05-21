package com.creatorjohn.helpers.json;

import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Objects;

public class PowerUpDeserializer implements JsonDeserializer<PowerUp> {

    @Override
    public PowerUp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) return null;

        JsonObject obj = json.getAsJsonObject();
        PowerUp.Type type = PowerUp.Type.valueOf(obj.get("type").getAsString());

        PowerUp out;

        switch (type) {
            case BOMB -> {
                if (JConfig.isNotObject( obj.get("position"))) out = null;
                else {
                    Position position = MyGson.instance.fromJson(obj.get("position"), Position.class);
                    out = new Bomb(position);
                }
            }
            case BOMBER -> {
                if (JConfig.isNotObject(obj.get("position"))) out = null;
                else if (!obj.has("direction")) out = null;
                else {
                    Position position = MyGson.instance.fromJson(obj.get("position"), Position.class);
                    Bomber.Direction direction = Bomber.Direction.valueOf(obj.get("direction").getAsString());
                    out = new Bomber(position, direction);
                }
            }
            case FARM -> {
                if (JConfig.isNotArray(obj.get("positions"))) out = null;
                else {
                    List<Position> positions = obj
                            .getAsJsonArray("positions")
                            .asList()
                            .stream()
                            .map(el -> {
                                if (!el.isJsonObject()) return null;
                                return MyGson.instance.fromJson(el.getAsJsonObject(), Position.class);
                            })
                            .filter(Objects::nonNull)
                            .toList();
                    out = new Farm(positions);
                }
            }
            case RADAR -> {
                if (JConfig.isNotObject(obj.get("position"))) out = null;
                else {
                    Position position = MyGson.instance.fromJson(obj.get("position"), Position.class);
                    out = new Radar(position);
                }
            }
            default -> out = null;
        }

        return out;
    }
}
