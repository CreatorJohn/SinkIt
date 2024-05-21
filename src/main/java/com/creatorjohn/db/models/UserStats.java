package com.creatorjohn.db.models;

import com.creatorjohn.helpers.JConfig;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.*;

import java.lang.reflect.Type;

final public class UserStats implements JsonDeserializer<UserStats> {
    private int gamesWon = 0;
    private int gamesLost = 0;

    public UserStats() {}

    public UserStats(int gamesWon, int gamesLost) {
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
    }

    public void updateGamesWonBy(int value) {
        gamesWon += value;
    }

    public void updateGamesWon(int value) {
        gamesWon = value;
    }

    public void updateGamesLostBy(int value) {
        gamesLost += value;
    }

    public void updateGamesLost(int value) {
        gamesLost = value;
    }

    public int gamesWon() {
        return gamesWon;
    }

    public int gamesLost() {
        return gamesLost;
    }

    @Override
    public UserStats deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (JConfig.isNotObject(json)) return null;

        JsonObject obj = json.getAsJsonObject();

        if (JConfig.isNotPrimitive(obj.get("gamesWon"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("gamesLost"))) return null;
        else if (JConfig.isNotPrimitive(obj.get("userID"))) return null;

        int gamesWon = obj.get("gamesWon").getAsInt();
        int gamesLost = obj.get("gamesLost").getAsInt();

        return new UserStats(gamesWon, gamesLost);
    }

    @Override
    public String toString() {
        return MyGson.instance.toJson(this);
    }
}
