package com.creatorjohn.helpers.json;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.Ship;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.powerups.PowerUp;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class EventDeserializer implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (JConfig.isNotObject(json)) return null;

        JsonObject obj = json.getAsJsonObject();

        if (JConfig.isNotPrimitive(obj.get("type"))) return null;

        Event.Type type = Event.Type.valueOf(obj.get("type").getAsString());
        Event out;

        switch (type) {
            case CREATE_GAME -> out = new CreateGameEvent(obj.get("gameID").getAsString());
            case JOIN_GAME -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new JoinGameEvent(obj.get("gameID").getAsString());
            }
            case INITIALIZE_GAME -> {
                if (JConfig.isNotArray(obj.get("ships"))) out = null;
                else out = new InitializeGameEvent(JConfig.convertArray("ships", obj, Ship.class));
            }
            case GAME_CREATED -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new GameCreatedEvent(obj.get("gameID").getAsString());
            }
            case UPDATE_GAME -> {
                if (JConfig.isNotArray(obj.get("tilesShot")) || JConfig.isNotArray(obj.get("usedPowerUps"))) out = null;
                else {
                    List<Position> tilesShot = JConfig.convertArray("tilesShot", obj, Position.class);
                    List<PowerUp> usedPowerUps = JConfig.convertArray("usedPowerUps", obj, PowerUp.class);
                    out = new UpdateGameEvent(tilesShot, usedPowerUps);
                }
            }
            case NEXT_ROUND -> {
                if (JConfig.isNotPrimitive(obj.get("myRound"))) out = null;
                else out = new NextRound(obj.get("myRound").getAsBoolean());
            }
            case GAME_FINISHED -> {
                if (JConfig.isNotPrimitive(obj.get("status"))) out = null;
                else out = new GameFinishedEvent(GameFinishedEvent.Status.valueOf(obj.get("status").getAsString()));
            }
            case DISCONNECT -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new DisconnectEvent(obj.get("gameID").getAsString());
            }
            default -> out = null;
        }

        return out;
    }
}
