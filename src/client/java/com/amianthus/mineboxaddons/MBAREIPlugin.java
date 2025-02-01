package com.amianthus.mineboxaddons;

import com.google.gson.JsonArray;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class MBAREIPlugin implements REIClientPlugin {
    @Override
    public void registerEntries(EntryRegistry registry) {
        List<Item> items = loadItemsFromJson();
        for (Item item : items) {
            EntryStack<ItemStack> entry = EntryStacks.of(new ItemStack(Items.STONE))
                    .setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, stack -> {
                        List<Text> tooltip = new ArrayList<>();
                        tooltip.add(Text.literal("§bRarity: §f" + item.rarity()));
                        tooltip.add(Text.literal("§6Level: §f" + item.level()));
                        item.stats().forEach((stat, values) -> {
                            String valueStr = values.get(0).getAsString(); // Get first value from JsonArray
                            if (values.size() > 1) {
                                valueStr += "-" + values.get(1).getAsString();
                            }
                            tooltip.add(Text.literal("§a" + stat + ": §f" + valueStr));
                        });
                        return tooltip;
                    });
            registry.addEntry(entry);
        }
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        Map<String, List<EntryStack<?>>> categoryMap = new HashMap<>();
        List<Item> items = loadItemsFromJson();

        for (Item item : items) {
            EntryStack<?> entry = EntryStacks.of(new ItemStack(Items.STONE))
                    .tooltip(Collections.emptyList());

            categoryMap.computeIfAbsent(item.category(), k -> new ArrayList<>())
                    .add(entry);
        }

        categoryMap.forEach((categoryId, entries) -> {
            CategoryIdentifier<?> reiCategory = CategoryIdentifier.of(Identifier.of("mineboxaddons", categoryId.toLowerCase())
            );

            // Create proper category registration
            registry.add(
                    new CustomItemCategory(
                            reiCategory,
                            Text.literal(categoryId),
                            EntryStacks.of(Items.BOOK) // Icon for category
                    )
            );

            // Add entries to category
            registry.addWorkstations(reiCategory, entries);
        });
    }

    // Simplified JSON loading (implement your actual loader here)
    private List<Item> loadItemsFromJson() {
        List<Item> items = new ArrayList<>();

        // Example item matching your JSON structure
        items.add(new Item(
                "test_item",
                "WEAPONS",
                "LEGENDARY",
                "base64",
                10,
                "Test Item",
                "Lore text",
                "Description text",
                Map.of(),
                Map.of(),
                null,
                List.of()
        ));
        return items;
    }

    // Required records (ensure these match your actual implementations)
    public record Item(String id, String category, String rarity, String image, int level,
                       String name, String lore, String description, Map<String, JsonArray> stats,
                       Map<String, JsonArray> damages, Recipe recipe, List<UsedInRecipe> usedInRecipes) {}

    public record UsedInRecipe(String type, String id, Item item, int amount) {}
    public record Ingredient(String type, String id, Item item, int amount) {}
    public record Recipe(String job, List<Ingredient> ingredients) {}
}