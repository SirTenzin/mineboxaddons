package com.amianthus.mineboxaddons.mineboxapi.bazaar.structures;

// Define the UsedInRecipe record
public record UsedInRecipe(
        String type,
        String id,
        Item item,
        int amount
) {}