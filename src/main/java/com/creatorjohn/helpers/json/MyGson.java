package com.creatorjohn.helpers.json;

import com.creatorjohn.helpers.Position;
import com.creatorjohn.helpers.entities.Ship;
import com.creatorjohn.helpers.events.Event;
import com.creatorjohn.helpers.entities.PowerUp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {
    final private static GsonBuilder baseBuilder = new GsonBuilder()
            .registerTypeAdapter(Position.class, new PositionDeserializer())
            .registerTypeAdapter(PowerUp.class, new PowerUpDeserializer())
            .registerTypeAdapter(Event.class, new EventDeserializer())
            .registerTypeAdapter(Ship.class, new ShipDeserializer());
    final public static Gson instance = baseBuilder.create();
}
