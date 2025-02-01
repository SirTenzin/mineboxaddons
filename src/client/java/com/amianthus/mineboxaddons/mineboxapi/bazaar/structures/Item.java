package com.amianthus.mineboxaddons.mineboxapi.bazaar.structures;

import com.amianthus.mineboxaddons.utils.ImageSerializer;
import com.google.gson.JsonArray;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public record Item(
        String id,
        String category,
        String rarity,
        @JsonAdapter(ImageSerializer.class) String image,
        int level,
        String name,
        String lore,
        String description,
        Map<String, JsonArray> stats,
        Map<String, JsonArray> damages,
        Recipe recipe,
        @SerializedName("used_in_recipes") List<UsedInRecipe> usedInRecipes
) {}

