package com.amianthus.mineboxaddons.mineboxapi.bazaar.structures;

import java.util.List;

// Define the Recipe record
public record Recipe(
        String job,
        List<Ingredient> ingredients
) {}
