package com.creatorjohn.db;

import com.creatorjohn.db.models.DataModel;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.logging.MyLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

final public class DatabaseHandler<T extends DataModel<T>> implements Database<T> {
    final private MyLogger logger = new MyLogger("Database Handler");
    final private Class<T> tClass;
    final private File file;
    final private boolean logFieldErrors;
    final private Set<String> uniqueKeys = new HashSet<>();

    public DatabaseHandler(@NotNull String filename, @NotNull Class<T> tClass, boolean logFieldErrors) {
        File file = new File(filename);
        this.logFieldErrors = logFieldErrors;
        this.tClass = tClass;

        try {
            if (file.exists() && !file.isFile()) logger.severe("Not file!");
            else if (file.exists()) logger.fine("Database reinitialized!");
            else if (file.createNewFile()) logger.fine("Database initialized!");
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            file = null;
        }

        this.file = file;
    }

    private @Nullable Method getMethod(String key) {
        try {
            return tClass.getMethod(key);
        } catch (NoSuchMethodException ignored) {
            if (logFieldErrors) logger.severe("Failed to get field for \"" + key + "\"!");
            return null;
        }
    }

    private @Nullable Object getMethodValue(String key, T item) {
        Method method = getMethod(key);

        if (method == null) return null;

        try {
            return method.invoke(item);
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return null;
        }
    }

    private <V> List<T> filter(V value, String key, List<T> items) {
        Method method = getMethod(key);

        if (method == null) return List.of();

        return items
                .stream()
                .filter(it -> checkItem(value, method, it))
                .toList();
    }

    private <V> @Nullable T get(V value, String key, List<T> items) {
        List<T> filtered = filter(value, key, items);

        if (filtered.isEmpty()) return null;
        else return filtered.getFirst();
    }

    private <V> boolean checkItem(V value, String key, T item) {
        return value == null || Objects.equals(value, getMethodValue(key, item));
    }

    private <V> boolean checkItem(V value, @NotNull Method method, T item) {
        try {
            return value == null || Objects.equals(value, method.invoke(item));
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return false;
        }
    }

    private boolean checkUniqueKeys(List<T> all, List<T> selected) {
        return uniqueKeys
                .stream()
                .allMatch(key -> selected
                        .stream()
                        .allMatch(item -> all.isEmpty() || all
                                .stream()
                                .noneMatch(it -> checkItem(getMethodValue(key, it), key, item))));
    }

    public void setUniqueKeys(List<String> keys) {
        this.uniqueKeys.addAll(keys);
    }

    @Override
    public boolean save(T item) {
        return save(List.of(item));
    }

    @Override
    public boolean save(List<T> items) {
        List<T> saved = new ArrayList<>(getAll());
        boolean canBeAdded = checkUniqueKeys(saved, items);

        if (!canBeAdded) return false;

        saved.addAll(items);

        try {
            FileWriter writer = new FileWriter(file, false);
            StringBuilder out = new StringBuilder();

            saved.forEach(item -> out
                    .append(MyGson.instance.toJson(item))
                    .append("\n"));

            writer.write(out.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<T> getAll() {
        try {
            ArrayList<T> out = new ArrayList<>();
            DataInputStream reader = new DataInputStream(new FileInputStream(file));
            String read = new String(reader.readAllBytes());

            for (String part : read.split("\n")) {
                if (part.isBlank()) continue;

                T parsed = MyGson.instance.fromJson(part, tClass);

                out.add(parsed);
            }

            reader.close();
            return out;
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            return List.of();
        }
    }

    @Override
    public <V> @NotNull List<T> getAll(V value, String key) {
        List<T> records = getAll();

        return filter(value, key, records);
    }

    @Override
    public <V> @Nullable T get(V value, String key) {
        return get(value, key, getAll());
    }

    @Override
    public <V> boolean update(V value, String key, T newItem) {
        List<T> records = getAll();
        T old = get(value, key, records);

        if (old == null) return false;

        int index = records.indexOf(old);

        records.set(index, newItem);

        return true;
    }

    @Override
    public <V> boolean delete(V value, String key) {
        ArrayList<T> records = new ArrayList<>(getAll());

        records.removeIf(it -> checkItem(value, key, it));

        return save(records);
    }

    @Override
    public boolean clear() {
        return save(List.of());
    }
}
