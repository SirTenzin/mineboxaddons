package com.amianthus.mineboxaddons;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CustomItemDisplay implements Display {
    private final MBAREIPlugin.Item item; // Your custom item data

    public CustomItemDisplay(MBAREIPlugin.Item item) {
        this.item = item;
    }

    // Required by REI: Define "inputs" (left side of recipe)
    @Override
    public List<EntryIngredient> getInputEntries() {
        // Use placeholder since these aren't real recipes
        return Collections.singletonList(
                EntryIngredient.of(EntryStacks.of(Items.STONE))
        );
    }

    // Required by REI: Define "outputs" (right side of recipe)
    @Override
    public List<EntryIngredient> getOutputEntries() {
        // Use placeholder
        return Collections.singletonList(
                EntryIngredient.of(EntryStacks.of(Items.STONE))
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return null;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    public MBAREIPlugin.Item getItem() {
        return item;
    }
}