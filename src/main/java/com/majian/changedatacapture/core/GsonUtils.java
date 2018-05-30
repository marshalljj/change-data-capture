package com.majian.changedatacapture.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;

public class GsonUtils {

    public static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Date.class, new JsonDateSerializer()).
            create();

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static class JsonDateSerializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(date.getTime());
        }
    }
}
