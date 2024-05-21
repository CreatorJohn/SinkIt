package com.creatorjohn.helpers;

import com.creatorjohn.helpers.entities.PowerUp;
import com.creatorjohn.helpers.json.MyGson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class JConfig {
    public static Font labelFont = new Font("Arial", Font.BOLD, 24);
    public static Font fieldFont = new Font("Arial", Font.PLAIN, 18);
    public static Font buttonFont = new Font("Arial", Font.BOLD, 18);
    public static Insets buttonInsets = new Insets(5, 5, 5, 5);
    public static int maxShipSize = 5;
    public static int maxShots = 5;
    //    public static int smallMapShipCount = 8;
    public static int smallMapShipCount = 1;
    public static int mediumMapShipCount = 11;
    public static int bigMapShipCount = 14;
    public static String serverAddress = "164.92.171.44";
//    public static String serverAddress = "127.0.0.1";
    public static int serverPort = 5000;

    public static boolean isNotPrimitive(JsonElement elem) {
        return !elem.isJsonPrimitive();
    }

    public static boolean isNotObject(JsonElement elem) {
        return !elem.isJsonObject();
    }

    public static boolean isNotArray(JsonElement elem) {
        return !elem.isJsonArray();
    }

    public static <T> List<T> convertArray(String key, JsonObject obj, Class<T> iClass) {
        return obj
                .getAsJsonArray(key)
                .asList()
                .stream()
                .map(it -> MyGson.instance.fromJson(it, iClass))
                .filter(Objects::nonNull)
                .toList();
    }

    public static void dialogError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void dialogInfo(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void dialogSuccess(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <In, Out> Out convert(In value, Class<Out> ref) {
        if (value.getClass().equals(ref)) return (Out) value;
        else return null;
    }
}
