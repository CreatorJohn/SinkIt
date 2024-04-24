package com.creatorjohn.db;

import com.creatorjohn.db.models.DataModel;
import com.creatorjohn.db.models.UserModel;
import com.creatorjohn.helpers.json.MyGson;
import com.creatorjohn.helpers.logging.MyLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

final public class DatabaseHandler<T extends DataModel<T>> implements Database<T> {
    final private MyLogger logger = new MyLogger("Database Handler");
    final private Class<T> tClass;
    final private File file;
    final private boolean logFieldErrors;
    private Set<String> uniqueKeys = new HashSet<>();

    public DatabaseHandler(@NotNull String filename, @NotNull Class<T> tClass, boolean logFieldErrors) {
        File file = new File(filename);
        this.tClass = tClass;
        this.logFieldErrors = logFieldErrors;

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

    @Nullable
    private Field getField(String key) {
        try {
            return tClass.getDeclaredField(key);
        } catch (NoSuchFieldException ignored) {
            if (logFieldErrors) logger.severe("Failed to get field for \"" + key + "\"!");
            return null;
        }
    }

    private <V> List<T> filter(V value, String key, List<T> items) {
        Field field = getField(key);

        if (field == null) return List.of();

        return items
                .stream()
                .filter(it -> checkItem(value, field, it))
                .toList();
    }

    private <V> boolean checkItem(V value, String key, T item) {
        Field field = getField(key);

        if (field == null) return false;

        try {
            return Objects.equals(value, field.get(item));
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    private <V> boolean checkItem(V value, Field field, T item) {
        try {
            return Objects.equals(value, field.get(item));
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    private boolean checkUniqueKeys(List<T> all, List<T> selected) {
        return uniqueKeys
                .stream()
                .noneMatch(key -> selected
                        .stream()
                        .anyMatch(item -> all
                                .stream()
                                .anyMatch(it -> checkItem(getFieldValue(key, it), key, it))));
    }

    private Object getFieldValue(String key, T item) {
        Field field = getField(key);

        try {
            if (field == null) return null;

            return field.get(item);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private <V> T get(V value, String key, List<T> items) {
        List<T> filtered = filter(value, key, items);

        if (filtered.isEmpty()) return null;
        else return filtered.getFirst();
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
        List<T> saved = getAll();
        boolean canBeAdded = checkUniqueKeys(saved, items) && checkUniqueKeys(items, items);

        if (!canBeAdded) return false;

        try {
            FileWriter writer = new FileWriter(file, false);
//            Records<T> records = new Records<>(items.stream().toList());

            writer.write(MyGson.instance.toJson(items));
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @NotNull
    @Override
    public List<T> getAll() {
        try {
            DataInputStream reader = new DataInputStream(new FileInputStream(file));
            String read = new String(reader.readAllBytes());
            Class<List<T>> lClass = (Class<List<T>>)(Object)List.class;
            List<T> data = MyGson.instance.fromJson(read, lClass);

            if (data == null) data = List.of();

            System.out.println(data);

            reader.close();
            return data;
        } catch (IOException e) {
            logger.severe(e.getLocalizedMessage());
            return List.of();
        }
    }

    @Override
    public <V> List<T> getAll(V value, String key) {
        List<T> records = getAll();

        return filter(value, key, records);
    }

    @Override
    public <V> T get(V value, String key) {
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

//    private record Records<T>(List<T> data) {}

    public static void main(String[] args) {
        UserModel geff = new UserModel("Geff", "heslo1");
        UserModel jimmy = new UserModel("Jimmy", "heslo2");
        DatabaseHandler<UserModel> db = new DatabaseHandler<>("./src/main/java/com/creatorjohn/db/data/users.json", UserModel.class, false);
        db.setUniqueKeys(List.of("username", "password"));

        if (db.save(geff)) System.out.println("Geff saved!");
        else System.out.println("Failed to save Geff!");

        System.out.println(db.getAll());

        if (db.save(jimmy)) System.out.println("Jimmy saved!");
        else System.out.println("Failed to save Jimmy!");

        System.out.println(db.getAll());
        System.out.println("Only age 18+: " + db.getAll(18, "age"));
    }
}
