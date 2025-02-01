package com.amianthus.mineboxaddons.mineboxapi.bazaar.structures;

public record Ingredient(
        String type,
        String id,
        Item item,
        int amount
) {}