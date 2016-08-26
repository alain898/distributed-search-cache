package com.maxent.dscache.common.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonUtils {
    private static final Gson gson = new Gson();
    private static final ObjectMapper jackson = new ObjectMapper();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(
                    String.format("Failed to parse json:%s into object:%s. Exception:%s", json, classOfT.getName(), e));
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(
                    String.format("Failed to parse json:%s into object:%s. Exception:%s", json, typeOfT.toString(), e));
        }
    }

    public static <T> T fromMap(Map map, Class<T> classOfT) {
        return jackson.convertValue(map, classOfT);
    }
}
