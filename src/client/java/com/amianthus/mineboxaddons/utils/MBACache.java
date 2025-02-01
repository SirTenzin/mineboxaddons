package com.amianthus.mineboxaddons.utils;

import java.util.HashMap;
import java.util.Map;

public class MBACache {
    private static final Map<String, String> cache = new HashMap<>();

    public static void put(String key, String value) {
        cache.put(key, value);
    }

    public static String get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    public static void clear() {
        cache.clear();
    }
}