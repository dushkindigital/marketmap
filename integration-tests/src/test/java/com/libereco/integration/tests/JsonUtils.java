package com.libereco.integration.tests;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public abstract class JsonUtils {

    public static String toJson(Map<String, String> properties) {
        Gson gson = new Gson();
        return gson.toJson(properties);
    }

    public static JsonObject toJsonObject(Map<String, String> properties) {
        JsonObject object = new JsonObject();
        for (Entry<String, String> entry : properties.entrySet()) {
            object.addProperty(entry.getKey(), entry.getValue());
        }
        return object;
    }

    public static String userJson(String username, String password) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("status", "ACTIVE");
        parameters.put("lastUpdated", new Date().toString());
        Gson gson = new Gson();
        String userJson = gson.toJson(parameters);
        return userJson;
    }

    public static String userJsonWithId(String username, String password, Long id) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("id", String.valueOf(id));
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("status", "ACTIVE");
        parameters.put("lastUpdated", new Date().toString());
        parameters.put("version", String.valueOf(0));
        Gson gson = new Gson();
        String userJson = gson.toJson(parameters);
        return userJson;
    }

    public static Map<String, String> toMap(String json) {
        Type type =
                new TypeToken<Map<String, String>>() {
                }.getType();
        Map<String, String> map =
                new Gson().fromJson(json, type);
        return map;
    }
}
