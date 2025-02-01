package com.amianthus.mineboxaddons.utils;

import org.apache.commons.lang3.tuple.Pair;

public class DurabilityManager {
    private static DurabilityManager instance;
    private Pair<Number, Number> durabilityMap = Pair.of(null, null);
    private Pair<String, Number> maxDurabilityMap = Pair.of(null, null);

    private DurabilityManager() {}

    public static DurabilityManager getInstance() {
        if (instance == null) {
            instance = new DurabilityManager();
        }
        return instance;
    }

    public void setMaxDurability(Number maxDurability) {
        durabilityMap = Pair.of(durabilityMap.getLeft(), maxDurability);
    }

    public Number getMaxDurability() {
        return durabilityMap.getRight();
    }

    public void setDurability(Number durability, Number maxDurability) {
        durabilityMap = Pair.of(durability, maxDurability);
    }

    public Pair<Number, Number> getDurability() {
        return durabilityMap;
    }

    public void saveMaxDurability(String itemName, Number maxDurability) {
        maxDurabilityMap = Pair.of(itemName, maxDurability);
    }

    public Number getMaxDurability(String itemName) {
        if (maxDurabilityMap.getLeft().equals(itemName)) {
            return maxDurabilityMap.getRight();
        }
        return null;
    }
}
