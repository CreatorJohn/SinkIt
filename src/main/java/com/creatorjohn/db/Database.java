package com.creatorjohn.db;

import com.creatorjohn.db.models.DataModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public sealed interface Database <T extends DataModel<T>> permits DatabaseHandler {
    /**
     * Unique keys are used for saving filtering and deleting items in database.
     * @param keys List of required unique item keys
     */
    void setUniqueKeys(List<String> keys);

    /**
     * Saves item to database if it is not already saved.
     * @param item Item to be saved
     * @return whether the operation was successful
     */
    boolean save(T item);

    /**
     * Saves list of items to database if all of them are not already in database.
     * @param items List of items to be saved
     * @return whether the operation was successful
     */
    boolean save(List<T> items);

    /**
     * Loads all items saved in database.
     * @return List of saved items
     */
    @NotNull List<T> getAll();

    /**
     * Loads all items with matching key from database.
     * @param value Required value
     * @param key Item key
     * @return List of matching items
     * @param <V> Type of required value
     */
    @NotNull <V> List<T> getAll(V value, String key);

    /**
     * Loads one item with matching key from database.
     * @param value Required value
     * @param key Item key
     * @return Matching item
     * @param <V> Type of required value
     */
    @Nullable <V> T get(V value, String key);

    /**
     * Updates item in database.
     * @param value Required value
     * @param key Item eky
     * @param newItem Item to be updated
     * @return whether the operation was successful
     * @param <V> Type of required value
     */
    <V> boolean update(V value, String key, T newItem);

    /**
     * Deletes items with matching key from database.
     * @param value Required value
     * @param key Item key
     * @return whether the operation was successful
     * @param <V> Type of required value
     */
    <V> boolean delete(V value, String key);

    /**
     * Deletes all items from database.
     * @return whether the operation was successful
     */
    boolean clear();
}
