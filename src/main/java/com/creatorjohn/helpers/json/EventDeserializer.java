package com.creatorjohn.helpers.json;

import com.creatorjohn.db.models.UserStats;
import com.creatorjohn.helpers.GameBoard;
import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.Ship;
import com.creatorjohn.helpers.events.*;
import com.creatorjohn.helpers.entities.PowerUp;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;
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
            case LOGIN -> {
                if (JConfig.isNotPrimitive(obj.get("username"))) out = null;
                else if (JConfig.isNotPrimitive(obj.get("password"))) out = null;
                else out = new LoginEvent(obj.get("username").getAsString(), obj.get("password").getAsString());
            }
            case REGISTER -> {
                if (JConfig.isNotPrimitive(obj.get("username"))) out = null;
                else if (JConfig.isNotPrimitive(obj.get("password"))) out = null;
                else out = new RegisterEvent(obj.get("username").getAsString(), obj.get("password").getAsString());
            }
            case LOGOUT -> out = new LogoutEvent();
            case LOGIN_RESPONSE -> {
                String error;

                if (JConfig.isNotPrimitive(obj.get("error"))) error = null;
                else error = obj.get("error").getAsString();

                return new LoginResponseEvent(error);
            }
            case REGISTER_RESPONSE -> {
                String error;

                if (JConfig.isNotPrimitive(obj.get("error"))) error = null;
                else error = obj.get("error").getAsString();

                return new RegisterResponseEvent(error);
            }
            case CREATE_GAME -> {
                if (JConfig.isNotPrimitive(obj.get("size"))) out = null;
                else if (!Arrays.stream(GameBoard.BoardSize.values()).map(GameBoard.BoardSize::name).toList().contains(obj.get("size").getAsString())) out = null;
                else out = new CreateGameEvent(GameBoard.BoardSize.valueOf(obj.get("size").getAsString()));
            }
            case GAME_CREATED -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new GameCreatedEvent(obj.get("gameID").getAsString());
            }
            case JOIN_GAME -> {
                if (JConfig.isNotPrimitive(obj.get("gameID"))) out = null;
                else out = new JoinGameEvent(obj.get("gameID").getAsString());
            }
            case GAME_JOINED -> {
                if (JConfig.isNotPrimitive(obj.get("success"))) out = null;
                else if (JConfig.isNotObject(obj.get("ships"))) out = null;
                else {
                    boolean success = Boolean.parseBoolean(obj.get("success").getAsString());
                    JsonObject sObj = obj.get("ships").getAsJsonObject();

                    List<Ship> myShips = JConfig.convertArray("my", sObj, Ship.class);
                    List<Ship> enemyShips = JConfig.convertArray("enemy", sObj, Ship.class);

                    out = new GameJoinedEvent(new GameJoinedEvent.ShipInfo(myShips, enemyShips), success);
                }
            }
            case PLAYER_JOINED -> {
                if (JConfig.isNotPrimitive(obj.get("username"))) out = null;
                else out = new PlayerJoinedEvent(obj.get("username").getAsString());
            }
            case PLAYER_LEFT -> out = new PlayerLeftEvent();
            case INITIALIZE_GAME -> {
                if (JConfig.isNotArray(obj.get("ships"))) out = null;
                else out = new InitializeGameEvent(JConfig.convertArray("ships", obj, Ship.class));
            }
            case GAME_INITIALIZED -> {
                if (JConfig.isNotPrimitive(obj.get("success"))) out = null;
                else out = new GameInitializedEvent(Boolean.parseBoolean(obj.get("success").getAsString()));
            }
            case UPDATE_GAME -> {
                if (JConfig.isNotArray(obj.get("tilesShot"))) out = null;
                else if (JConfig.isNotArray(obj.get("usedPowerUps"))) out = null;
                else if (JConfig.isNotPrimitive(obj.get("gameOver"))) out = null;
                else {
                    List<Position> tilesShot = JConfig.convertArray("tilesShot", obj, Position.class);
                    List<PowerUp> usedPowerUps = JConfig.convertArray("usedPowerUps", obj, PowerUp.class);
                    boolean gameOver = Boolean.parseBoolean(obj.get("gameOver").getAsString());
                    out = new UpdateGameEvent(tilesShot, usedPowerUps, gameOver);
                }
            }
            case GAME_UPDATED -> {
                if (JConfig.isNotPrimitive(obj.get("currentPlayer"))) out = null;
                else if (JConfig.isNotObject(obj.get("shotTiles"))) out = null;
                else if (JConfig.isNotObject(obj.get("powerUps"))) out = null;
                else if (JConfig.isNotPrimitive(obj.get("success"))) out = null;
                else {
                    String currentPlayer = obj.get("currentPlayer").getAsString();
                    JsonObject sObj = obj.get("shotTiles").getAsJsonObject();
                    JsonObject pObj = obj.get("powerUps").getAsJsonObject();
                    List<PowerUp> myPowerUps = JConfig.isNotArray(pObj.get("my")) ? List.of() : JConfig.convertArray("my", pObj, PowerUp.class);
                    List<PowerUp> enemyPowerUps = JConfig.isNotArray(pObj.get("enemy")) ? List.of() : JConfig.convertArray("enemy", pObj, PowerUp.class);
                    List<Position> myShotTiles = JConfig.isNotArray(sObj.get("my")) ? List.of() : JConfig.convertArray("my", sObj, Position.class);
                    List<Position> enemyShotTiles = JConfig.isNotArray(sObj.get("enemy")) ? List.of() : JConfig.convertArray("enemy", sObj, Position.class);
                    List<Ship> ships = JConfig.isNotArray(obj.get("ships")) ? List.of() : JConfig.convertArray("ships", obj, Ship.class);
                    boolean success = Boolean.parseBoolean(obj.get("success").getAsString());
                    GameUpdatedEvent.PowerUpsInfo pInfo = new GameUpdatedEvent.PowerUpsInfo(myPowerUps, enemyPowerUps);
                    GameUpdatedEvent.ShotTilesInfo sInfo = new GameUpdatedEvent.ShotTilesInfo(myShotTiles, enemyShotTiles);
                    out = new GameUpdatedEvent(currentPlayer, pInfo, sInfo, ships, success);
                }
            }
            case GAME_FINISHED -> {
                if (JConfig.isNotPrimitive(obj.get("status"))) out = null;
                else out = new GameFinishedEvent(GameFinishedEvent.Status.valueOf(obj.get("status").getAsString()));
            }
            case DISCONNECT -> out = new DisconnectEvent();
            case STATISTICS_REQUEST -> out = new StatisticsRequestEvent();
            case STATISTICS_RESPONSE -> {
                if (JConfig.isNotObject(obj.get("stats"))) out = null;
                else out = new StatisticsResponseEvent(MyGson.instance.fromJson(obj.get("stats"), UserStats.class));
            }
            default -> out = null;
        }

        return out;
    }
}
