package com.amianthus.mineboxaddons.screens;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.Items;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.Ingredient;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.Item;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.UsedInRecipe;
import com.amianthus.mineboxaddons.utils.ChatUtils;
import com.amianthus.mineboxaddons.utils.ImageRenderer;
import com.amianthus.mineboxaddons.utils.ImageUtils;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.amianthus.mineboxaddons.screens.badges.Badges.raritySizes;

@SuppressWarnings({"DuplicatedCode"})
public class ViewItemScreen extends BaseUIModelScreen<FlowLayout> {

    private static Item item;

    public ViewItemScreen(Item selectedItem) {
        super(FlowLayout.class, DataSource.asset(Identifier.of("mineboxaddons", "view_item")));
        item = selectedItem;
    }

    private final Map<String, Color> damageTypes = new HashMap<>();
    {
        damageTypes.put("AIR", Color.ofRgb(Colors.GRAY));
        damageTypes.put("EARTH", Color.ofRgb(0x964B00));
        damageTypes.put("FIRE", Color.RED);
        damageTypes.put("WATER", Color.ofRgb(Colors.CYAN));
        damageTypes.put("DEFENSE", Color.ofFormatting(Formatting.DARK_GREEN));
        damageTypes.put("HEALTH", Color.RED);
        damageTypes.put("STRENGTH", Color.ofRgb(0x964B00));
        damageTypes.put("WISDOM", Color.ofRgb(Colors.BLUE));
        damageTypes.put("AGILITY", Color.ofRgb(Colors.GREEN));
        damageTypes.put("INTELLIGENCE", Color.ofRgb(Colors.PURPLE));
        damageTypes.put("FORTUNE", Color.ofRgb(Colors.YELLOW));
        damageTypes.put("LUCK", Color.ofRgb(Colors.LIGHT_YELLOW));
    }

    @Override
    protected void build(FlowLayout rootLayout) {
        FlowLayout itemNameContainer = rootLayout.childById(FlowLayout.class, "itemNameContainer");
        FlowLayout itemRecipeContainer = rootLayout.childById(FlowLayout.class, "recipeContainer");
        FlowLayout itemUsedInRecipesContainer = rootLayout.childById(FlowLayout.class, "usedInRecipeContainer");
        FlowLayout itemSidebar = rootLayout.childById(FlowLayout.class, "itemSidebar");
        LabelComponent itemName = rootLayout.childById(LabelComponent.class, "itemName");
        LabelComponent itemSidebarName = rootLayout.childById(LabelComponent.class, "itemSidebarName");
        LabelComponent itemDescription = rootLayout.childById(LabelComponent.class, "itemDescription");
        LabelComponent itemRecipeLabel = rootLayout.childById(LabelComponent.class, "itemRecipeLabel");
        LabelComponent itemUsedInRecipesLabel = rootLayout.childById(LabelComponent.class, "itemUsedInRecipesLabel");
        FlowLayout itemImage = rootLayout.childById(FlowLayout.class, "itemImage");
        itemName.text(Text.of(item.name() + " | Level " + item.level()));
        itemSidebarName.text(Text.of(item.name())).color(Color.BLACK);
        itemDescription.text(Text.of(item.lore() + "\n\n" + item.description()));
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        try {
            BufferedImage rarityImage = ImageUtils.getTextureAsBufferedImage(MinecraftClient.getInstance().getResourceManager(), "textures/images/rarity/" + item.rarity().toLowerCase() + ".png");
            Identifier badgeIdentifier = ImageRenderer.createTextureFromBufferedImage(rarityImage, item.rarity().toLowerCase());
            itemNameContainer.child(0, Components.texture(
                    badgeIdentifier,
                    0, 0,
                    raritySizes.get(item.rarity().toLowerCase()).width, raritySizes.get(item.rarity().toLowerCase()).height,
                    raritySizes.get(item.rarity().toLowerCase()).width, raritySizes.get(item.rarity().toLowerCase()).height
            ).margins(Insets.of(0, 0, 0, 5)));

            if(item.image() != null) {
                BufferedImage itemImageBuffered = ImageUtils.decodeBase64ToImage(item.image());
                Identifier itemImageIdentifier = ImageRenderer.createTextureFromBufferedImage(itemImageBuffered, item.id());
                itemImage.child(0, Components.texture(
                        itemImageIdentifier,
                        0, 0,
                        128, 128,
                        128, 128
                ));
            }

            if(item.damages() != null) {
                for (String key : item.damages().keySet()) {
                    System.out.println(key);
                    System.out.println(item.damages().get(key));
                    FlowLayout itemDamagesChip = Containers.horizontalFlow(Sizing.fixed(textRenderer.getWidth(key) + 10), Sizing.fixed(20));
                    itemDamagesChip.margins(Insets.of(15))
                        .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                        .surface(Surface.DARK_PANEL);
                    LabelComponent itemDamageLabel = Components.label(Text.of(key));
                    itemDamageLabel.horizontalTextAlignment(HorizontalAlignment.CENTER)
                    .verticalTextAlignment(VerticalAlignment.CENTER)
                    .color(damageTypes.get(key))
                    .margins(Insets.of(5));
                    itemDamagesChip.child(0, itemDamageLabel);
                    String itemDamage = item.damages().get(key).getAsJsonArray().get(0) == item.damages().get(key).getAsJsonArray().get(1)
                            ? String.valueOf(item.damages().get(key).getAsJsonArray().get(0))
                            : item.damages().get(key).getAsJsonArray().get(0) + " - " + item.damages().get(key).getAsJsonArray().get(1);
                    LabelComponent itemDamageStatLabel = Components.label(Text.of(itemDamage));
                    itemDamageStatLabel.color(damageTypes.get(key));
                    if (key.equals(item.stats().keySet().toArray()[item.stats().size() - 1])) {
                        itemDamageStatLabel.margins(Insets.of(0, 5, 0, 0));
                    }
                    itemSidebar.child(itemDamagesChip);
                    itemSidebar.child(itemDamageStatLabel);
                }
            }

            if(item.stats() != null) {
                for (String key : item.stats().keySet()) {
                    System.out.println(key);
                    System.out.println(item.stats().get(key));
                    FlowLayout itemStatsChip = Containers.horizontalFlow(Sizing.fixed(textRenderer.getWidth(key) + 10), Sizing.fixed(20));
                    itemStatsChip.margins(Insets.of(15))
                            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                            .surface(Surface.DARK_PANEL);
                    LabelComponent itemStatLabel = Components.label(Text.of(key));
                    itemStatLabel.horizontalTextAlignment(HorizontalAlignment.CENTER)
                            .verticalTextAlignment(VerticalAlignment.CENTER)
                            .color(damageTypes.get(key))
                            .margins(Insets.of(5));
                    itemStatsChip.child(0, itemStatLabel);
                    String itemStat = item.stats().get(key).getAsJsonArray().get(0) == item.stats().get(key).getAsJsonArray().get(1)
                            ? String.valueOf(item.stats().get(key).getAsJsonArray().get(0))
                            : item.stats().get(key).getAsJsonArray().get(0) + " - " + item.stats().get(key).getAsJsonArray().get(1);
                    LabelComponent itemDamageStatLabel = Components.label(Text.of(itemStat));
                    itemDamageStatLabel.color(damageTypes.get(key));
                    if (key.equals(item.stats().keySet().toArray()[item.stats().size() - 1])) {
                        itemDamageStatLabel.margins(Insets.of(0, 5, 0, 0));
                    }
                    itemSidebar.child(itemStatsChip);
                    itemSidebar.child(itemDamageStatLabel);
                }
            }

            if(item.recipe() != null) displayRecipe(item, itemRecipeLabel, itemRecipeContainer);
            if(item.usedInRecipes() != null) displayUsedInRecipes(item, itemUsedInRecipesLabel, itemUsedInRecipesContainer);
            else itemUsedInRecipesLabel.text(Text.of("This item has 0 related recipes."));

        } catch (Exception e) {
            MineboxAddonsClient.LOGGER.error(e.toString());
        }
    }

    private static FlowLayout createItemChip(String itemName, int amount, String image, String id, boolean showAmount) {
        FlowLayout chip = Containers.horizontalFlow(Sizing.content(5), Sizing.fixed(32));
        chip.margins(Insets.of(15))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .surface(Surface.DARK_PANEL);

        addImageToChip(chip, image, id);

        LabelComponent labelText = showAmount ?
                Components.label(Text.of(amount + "x " + itemName)) :
                Components.label(Text.of(itemName));
        chip.child(labelText);
        chip.cursorStyle(CursorStyle.HAND);
        chip.mouseDown().subscribe((mouseButton, x, y) -> onClick(mouseButton, x, y, id));
        labelText.cursorStyle(CursorStyle.HAND);
        labelText.mouseDown().subscribe((mouseButton, x, y) -> onClick(mouseButton, x, y, id));
        return chip;
    }

    private static FlowLayout createVanillaItemChip(String id, int amount) {
        FlowLayout chip = Containers.horizontalFlow(Sizing.content(5), Sizing.fixed(32));
        chip.margins(Insets.of(15))
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
                .surface(Surface.DARK_PANEL);

        net.minecraft.item.Item mcItem = Registries.ITEM.get(Identifier.of(id));

        if (mcItem != net.minecraft.item.Items.AIR) {
            Identifier itemTexture = Identifier.of("minecraft", "textures/item/" + id + ".png");
            TextureComponent image = Components.texture(itemTexture, 0, 0, 32, 32, 32, 32);
            chip.child(image);
            LabelComponent labelText = Components.label(
                    Text.literal(amount + "x ").append(Text.translatable(mcItem.getTranslationKey()))
            );
            chip.child(labelText);
        }

        chip.tooltip(Text.literal(amount + "x ")
                .append(Text.translatable(mcItem.getTranslationKey())));
        return chip;
    }

    private static void addImageToChip(FlowLayout chip, String image, String id) {
        if (image != null && !image.isEmpty()) {
            Identifier imageIdentifier = ImageRenderer.createTextureFromBase64(image, id);
            TextureComponent imageComponent = Components.texture(
                    imageIdentifier,
                    0, 0,
                    32, 32,
                    32, 32
            );
            imageComponent.margins(Insets.of(5));
            imageComponent.mouseDown().subscribe((mouseButton, x, y) -> onClick(mouseButton, x, y, id));
            imageComponent.cursorStyle(CursorStyle.HAND);
            chip.child(imageComponent);
        } else {
            LabelComponent imageComponent = Components.label(Text.of("N/A"));
            imageComponent.cursorStyle(CursorStyle.HAND);
            chip.child(imageComponent);
            imageComponent.mouseDown().subscribe((mouseButton, x, y) -> onClick(mouseButton, x, y, id));
        }
    }

    private static Item resolveItem(String id, Item item) {
        if (item == null) {
            Item tmpItem = new Items().getItemByID(id);
            if (tmpItem != null) return tmpItem;
        }
        return item;
    }

    private static void displayRecipe(Item item, LabelComponent itemRecipeLabel, FlowLayout itemRecipeContainer) {
        if (item.recipe() != null) {
            itemRecipeLabel.text(
                            Text.literal("You can craft this at the ")
                                    .append(Text.literal(ChatUtils.toCapitalCase(item.recipe().job())))
                                    .append(Text.literal("."))
                    )
                    .color(Color.ofArgb(Colors.BLACK));

            for (Ingredient ingredient : item.recipe().ingredients()) {
                if (Objects.equals(ingredient.type(), "custom")) {
                    Item ingredientItem = resolveItem(ingredient.id(), ingredient.item());
                    if (ingredientItem != null) {
                        FlowLayout chip = createItemChip(
                                ingredientItem.name(),
                                ingredient.amount(),
                                ingredientItem.image(),
                                ingredientItem.id(),
                                true
                        );
                        chip.tooltip(Text.of(ingredientItem.name() + " x" + ingredient.amount()));
                        itemRecipeContainer.child(chip);
                    }
                } else {
                    itemRecipeContainer.child(createVanillaItemChip(ingredient.id(), ingredient.amount()));
                }
            }
        } else {
            itemRecipeLabel.text(Text.of("This item cannot be crafted."));
        }
    }

    private static void displayUsedInRecipes(Item item, LabelComponent itemUsedInRecipesLabel,
                                            FlowLayout itemUsedInRecipesContainer) {
//        MineboxAddonsClient.LOGGER.info("Used in recipes: {}", item.usedInRecipes().toString());
        if (!item.usedInRecipes().isEmpty()) {
            itemUsedInRecipesLabel.text(Text.of("This item has " +
                            item.usedInRecipes().size() + " related recipes."));

            for (UsedInRecipe uir : item.usedInRecipes()) {
                Item uirItem = resolveItem(uir.id(), uir.item());
                if (uirItem != null) {
                    FlowLayout chip = createItemChip(
                            uirItem.name(),
                            uir.amount(),
                            uirItem.image(),
                            uirItem.id(),
                            false
                    );
                    itemUsedInRecipesContainer.child(chip);
                }
            }
        } else {
            itemUsedInRecipesLabel.text(Text.of("This item cannot be crafted."));
        }
    }

    private static boolean onClick(double mouseButton, double x, int y, String id) {
        AtomicReference<Item> item = new AtomicReference<>();
        CompletableFuture.runAsync(() -> {
            Item prefetchedItem = new Items().getItemByID(id);
            if(prefetchedItem != null) item.set(prefetchedItem);
            MinecraftClient.getInstance().send(() -> {
                if (item.get() != null) {
                    MinecraftClient.getInstance().setScreen(new ViewItemScreen(item.get()));
                }
            });
        });
        return true;
    }
}
