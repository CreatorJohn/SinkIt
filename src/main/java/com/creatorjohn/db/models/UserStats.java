package com.creatorjohn.db.models;

import com.creatorjohn.helpers.json.JConfig;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.*;

import java.lang.reflect.Type;

final public class UserStats implements JsonDeserializer<UserStats> {
    private int gamesWon = 0;
    private int gamesLost = 0;
    private int shipsDestroyed = 0;
    private int powerUpsUsed = 0;

    public UserStats() {}

    public UserStats(int gamesWon, int gamesLost, int shipsDestroyed, int powerUpsUsed) {
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.shipsDestroyed = shipsDestroyed;
        this.powerUpsUsed = powerUpsUsed;
    }

    void updateGamesWonBy(int value) {
        gamesWon += value;
    }

    void updateGamesWon(int value) {
        gamesWon = value;
    }

    void updateGamesLostBy(int value) {
        gamesLost += value;
    }

    void updateGamesLost(int value) {
        gamesLost = value;
    }

    void updateShipsDestroyedBy(int value) {
        shipsDestroyed += value;
    }

    void updateShipsDestroyed(int value) {
        shipsDestroyed = value;
    }

    void updatePowerUpsUsedBy(int value) {
        powerUpsUsed += value;
    }

    void updatePowerUpsUsed(int value) {
        powerUpsUsed = value;
    }

    public int gamesWon() {
        return gamesWon;
    }

    public int gamesLost() {
        return gamesLost;
    }

    public int shipsDestroyed() {
        return shipsDestroyed;
    }

    public int powerUpsUsed() {
        return powerUpsUsed;
    }

    @Override
    public UserStats deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (JConfig.isNotObject(json)) return null;

        JsonObject obj = json.getAsJsonObject();

        if (JConfig.isNotPrimitive(obj.get("gamesWon"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("gamesLost"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("shipsDestroyed"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("userID"))) return null;

        int gamesWon = obj.get("gamesWon").getAsInt();
        int gamesLost = obj.get("gamesLost").getAsInt();
        int shipsDestroyed = obj.get("shipsDestroyed").getAsInt();
        int powerUpsUsed = obj.get("powerUpsUsed").getAsInt();

        return new UserStats(gamesWon, gamesLost, shipsDestroyed, powerUpsUsed);
    }

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }
}
