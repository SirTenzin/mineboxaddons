package com.amianthus.mineboxaddons.utils;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ImageSerializer implements JsonDeserializer<String>, JsonSerializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        // Convert empty string to null when deserializing
        if (json.isJsonPrimitive() && json.getAsString().isEmpty()) {
            return null;
        }
        return json.getAsString();
    }

    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        // Convert null or empty string to JSON null when serializing
        if (src == null || src.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(src);
    }
}


