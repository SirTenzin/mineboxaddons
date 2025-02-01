package com.amianthus.mineboxaddons.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonReader {

    private static final String RESOURCE_BASE = "assets/mineboxaddons/";

    public static JsonArray readJsonFileArray(String relativePath) {
        String fullPath = RESOURCE_BASE + relativePath;
        InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(fullPath);

        if (inputStream == null) {
            throw new RuntimeException("Resource not found: " + fullPath);
        }

        try (Reader reader = new InputStreamReader(inputStream)) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + fullPath, e);
        }
    }
}