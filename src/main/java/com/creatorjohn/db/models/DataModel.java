package com.creatorjohn.db.models;

import com.google.gson.JsonDeserializer;

import java.util.Map;

public sealed interface DataModel<T> extends JsonDeserializer<T> permits UserModel {
    Map.Entry<String, T> toMapEntry();
}
