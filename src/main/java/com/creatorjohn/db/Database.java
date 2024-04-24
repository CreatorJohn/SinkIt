package com.creatorjohn.db;

import com.creatorjohn.db.models.DataModel;

import java.util.List;
import java.util.Map;

public sealed interface Database <T extends DataModel<T>> permits DatabaseHandler {
    void setUniqueKeys(List<String> keys);

    boolean save(T item);

    boolean save(List<T> items);

    List<T> getAll();

    <V> List<T> getAll(V value, String key);

    <V> T get(V value, String key);

    <V> boolean update(V value, String key, T newItem);

    <V> boolean delete(V value, String key);

    boolean clear();
}
