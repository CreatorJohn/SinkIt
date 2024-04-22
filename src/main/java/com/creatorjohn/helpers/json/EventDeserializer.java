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
            case CREATE_GAME -> out = new CreateGameEvent();
            case GAME_CREATED -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new GameCreatedEvent(obj.get("gameID").getAsString());
            }
            case JOIN_GAME -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new JoinGameEvent(obj.get("gameID").getAsString());
            }
            case GAME_JOINED -> {
                if (JConfig.isNotArray(obj.get("ships"))) out = null;
                else if (JConfig.isNotArray(obj.get("powerUps"))) out = null;
                else if (JConfig.isNotArray(obj.get("shotTiles"))) out = null;
                else if (JConfig.isNotArray(obj.get("revealedTiles"))) out = null;
                else {
                    List<Ship> ships = JConfig.convertArray("ships", obj, Ship.class);
                    List<PowerUp> powerUps = JConfig.convertArray("powerUps", obj, PowerUp.class);
                    List<Position> shotTiles = JConfig.convertArray("shotTiles", obj, Position.class);
                    List<Position> revealedTiles = JConfig.convertArray("revealedTiles", obj, Position.class);
                    out = new GameJoinedEvent(ships, powerUps, shotTiles, revealedTiles);
                }
            }
            case PLAYER_JOINED -> out = new PlayerJoinedEvent();
            case PLAYER_LEFT -> out = new PlayerLeftEvent();
            case INITIALIZE_GAME -> {
                if (JConfig.isNotArray(obj.get("ships"))) out = null;
                else out = new InitializeGameEvent(JConfig.convertArray("ships", obj, Ship.class));
            }
            case UPDATE_GAME -> {
                if (JConfig.isNotArray(obj.get("tilesShot")) || JConfig.isNotArray(obj.get("usedPowerUps"))) out = null;
                else {
                    List<Position> tilesShot = JConfig.convertArray("tilesShot", obj, Position.class);
                    List<PowerUp> usedPowerUps = JConfig.convertArray("usedPowerUps", obj, PowerUp.class);
                    out = new UpdateGameEvent(tilesShot, usedPowerUps);
                }
            }
            case GAME_UPDATED -> {
                if (JConfig.isNotPrimitive(obj.get("currentPlayer"))) out = null;
                else if (JConfig.isNotArray(obj.get("shotTiles"))) out = null;
                else {
                    String currentPlayer = obj.get("currentPlayer").getAsString();
                    List<Position> shotTiles = JConfig.convertArray("shotTiles", obj, Position.class);
                    out = new GameUpdatedEvent(currentPlayer, shotTiles);
                }
            }
            case GAME_FINISHED -> {
                if (JConfig.isNotPrimitive(obj.get("status"))) out = null;
                else out = new GameFinishedEvent(GameFinishedEvent.Status.valueOf(obj.get("status").getAsString()));
            }
            case DISCONNECT -> out = new DisconnectEvent();
            default -> out = null;
        }

        return out;
    }
}
