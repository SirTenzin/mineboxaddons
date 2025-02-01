package com.amianthus.mineboxaddons.listeners;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.toasts.CustomImageToast;
import com.amianthus.mineboxaddons.utils.DurabilityManager;
import com.amianthus.mineboxaddons.utils.ShopTimer;
import com.amianthus.mineboxaddons.widgets.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class EndTickListener implements ClientTickEvents.EndTick {
    private ItemStack previousMainHandStack = ItemStack.EMPTY; // Track the previous main hand item
    private DurabilityWidget durabilityWidget;
    private final AtomicReference<BakeryWidget> bakeryWidgetRef = new AtomicReference<>();
    private final AtomicReference<CheeseWidget> cheeseWidgetRef = new AtomicReference<>();
    private final AtomicReference<CocktailWidget> cocktailWidgetRef = new AtomicReference<>();
    private final AtomicReference<CoffeeWidget> coffeeWidgetRef = new AtomicReference<>();
    private final Map<String, Boolean> shopRefreshed = new HashMap<>();

    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return; // Ensure the player exists

        ItemStack currentMainHandStack = player.getMainHandStack(); // Get the current main hand item
        long currentTime = System.currentTimeMillis(); // Get the current system time
        int bakeryStatus = ShopTimer.getMinutesRemaining(ShopTimer.Shop.BAKERY, currentTime);
        int cheeseStatus = ShopTimer.getMinutesRemaining(ShopTimer.Shop.CHEESE, currentTime);
        int cocktailStatus = ShopTimer.getMinutesRemaining(ShopTimer.Shop.COCKTAIL, currentTime);
        int coffeeStatus = ShopTimer.getMinutesRemaining(ShopTimer.Shop.COFFEE, currentTime);
        String bakeryStatusText = ShopTimer.getShopStatus(ShopTimer.Shop.BAKERY, currentTime);
        String cheeseStatusText = ShopTimer.getShopStatus(ShopTimer.Shop.CHEESE, currentTime);
        String cocktailStatusText = ShopTimer.getShopStatus(ShopTimer.Shop.COCKTAIL, currentTime);
        String coffeeStatusText = ShopTimer.getShopStatus(ShopTimer.Shop.COFFEE, currentTime);

        // Check if the main hand item has changed
        if (!ItemStack.areEqual(currentMainHandStack, previousMainHandStack)) {
            previousMainHandStack = currentMainHandStack.copy(); // Update the previous item
            String itemName = currentMainHandStack.getName().getString();
            String firstLetter = itemName.charAt(0) + "";
            if(!firstLetter.toLowerCase().matches("[a-z]")) return;
            AtomicReference<Number> maxDurability = new AtomicReference<>();
            maxDurability.set(DurabilityManager.getInstance().getMaxDurability());

            if (!currentMainHandStack.isEmpty()) {
                List<Text> tooltip = getTooltipText(currentMainHandStack, player);
                StringBuilder fullTooltip = new StringBuilder();
                for (Text text : tooltip) {
                    String durabilityString = text.getString();
                    fullTooltip.append(durabilityString).append("\n");
                    if(durabilityString.contains("Durability")) {
                        // If we haven't saved it yet, find it and save it
                        if(durabilityWidget == null) {
                            // find it
                            durabilityWidget = (DurabilityWidget) HUDManager.INSTANCE.getWidget("MineboxAddons:DurabilityWidget");
                            // if we found it, enable it
                            if(durabilityWidget != null) {
                                // if it's not enabled, enable it
                                if(!durabilityWidget.isEnabled()) {
                                    durabilityWidget.toggleEnabled();
                                }
                            } else {
                                // if we didn't find it, create it and enable it
                                durabilityWidget = new DurabilityWidget();
                                HUDManager.INSTANCE.registerWidget(durabilityWidget);
                                durabilityWidget.toggleEnabled();
                            }
                        } else {
                            // if it's not enabled, enable it
                            if(!durabilityWidget.isEnabled()) {
                                durabilityWidget.toggleEnabled();
                            }
                        }
                        MineboxAddonsClient.LOGGER.info("Main hand slot changed. Tooltip:");
                        MineboxAddonsClient.LOGGER.info(" - {}", durabilityString);
                        // Split using \\D+
                        String[] numbers = durabilityString.split("\\D+"); // Splits on one or more non-digit characters
                        String durabilityStr = durabilityString.split(": ")[1].trim().split(" ")[0];
                        Number durability;
                        if(durabilityStr.contains("/")) {
                            durability = Integer.parseInt(durabilityStr.split("/")[0]);
                            maxDurability = new AtomicReference<>(Integer.parseInt(durabilityStr.split("/")[1]));
                        } else {
                            durability = Integer.parseInt(durabilityStr);
                            maxDurability = new AtomicReference<>(-1);
                        }
                        DurabilityManager.getInstance().setDurability(durability, maxDurability.get());
                        break;
                    }
                }
                // If the tooltip doesn't contain durability, disable the durability widget
                if(!fullTooltip.toString().contains("Durability")) {
                    // if we haven't saved it yet, find it and save it
                    // if we found it, disable it
                    if(durabilityWidget != null) {
                        // if it's enabled, disable it
                        if(durabilityWidget.isEnabled()) {
                            durabilityWidget.toggleEnabled();
                        }
                    } else {
                        // if we didn't save it, find it and disable it
                       durabilityWidget = (DurabilityWidget) HUDManager.INSTANCE.getWidget("MineboxAddons:DurabilityWidget");
                       // if we found it, disable it
                        if (durabilityWidget != null) {
                            // if it's enabled, disable it
                            if (durabilityWidget.isEnabled()) {
                                durabilityWidget.toggleEnabled();
                            }
                        }
                        // otherwise, do nothing
                    }
                }
            } else {
                System.out.println("Main hand slot is now empty.");
            }
        }

        if(MineboxAddonsClient.CONFIG.bakeryTimer()) updateOrCreateWidgetTime(bakeryWidgetRef, "MineboxAddons:BakeryWidget", bakeryStatusText, BakeryWidget::new, BakeryWidget.class);
        if(MineboxAddonsClient.CONFIG.coffeeTimer()) updateOrCreateWidgetTime(coffeeWidgetRef, "MineboxAddons:CoffeeWidget", coffeeStatusText, CoffeeWidget::new, CoffeeWidget.class);
        if(MineboxAddonsClient.CONFIG.cheeseTimer()) updateOrCreateWidgetTime(cheeseWidgetRef, "MineboxAddons:CheeseWidget", cheeseStatusText, CheeseWidget::new, CheeseWidget.class);
        if(MineboxAddonsClient.CONFIG.cocktailTimer()) updateOrCreateWidgetTime(cocktailWidgetRef, "MineboxAddons:CocktailWidget", cocktailStatusText, CocktailWidget::new, CocktailWidget.class);

        if(MineboxAddonsClient.CONFIG.showToasts()) handleShopToasts(Map.of(
                "cheese", cheeseStatus,
                "coffee", coffeeStatus,
                "cocktail", cocktailStatus,
                "bakery", bakeryStatus
        ), shopRefreshed);
    }

    // Generate the tooltip text for an item
    private List<Text> getTooltipText(ItemStack stack, PlayerEntity player) {
        // Create a TooltipContext (e.g., NORMAL or ADVANCED)
        TooltipContext context = TooltipContext.create(player.getWorld());
        // Specify the TooltipType (e.g., NORMAL or ADVANCED)
        TooltipType type = TooltipType.BASIC;

        // Call the getTooltip method
        return stack.getTooltip(context, player, type);
    }

    private <T extends HUDWidget> void updateOrCreateWidgetTime(
            AtomicReference<T> widgetRef,
            String widgetIdentifier,
            String status,
            Supplier<T> widgetConstructor,
            Class<T> widgetClass // Type token for safe casting
    ) {
        T widget = widgetRef.get();

        if (widget != null) {
            widget.updateTime(status);
        } else {
            HUDWidget fetchedWidget = HUDManager.INSTANCE.getWidget(widgetIdentifier);
            if (widgetClass.isInstance(fetchedWidget)) {
                widget = widgetClass.cast(fetchedWidget); // Safe cast
                widget.updateTime(status);
            } else {
                widget = widgetConstructor.get();
                HUDManager.INSTANCE.registerWidget(widget);
                widget.updateTime(status);
            }
            widgetRef.set(widget);
        }
    }

    public void handleShopToasts(Map<String, Integer> shopStatuses, Map<String, Boolean> shopRefreshed) {
        shopStatuses.forEach((shop, status) -> {
            if (Objects.equals(status, 0)) {
                if (!shopRefreshed.getOrDefault(shop, false)) {
                    // Mark as refreshed immediately
                    shopRefreshed.put(shop, true);

                    // Send the toast
                    MinecraftClient.getInstance().send(() -> {
                        MinecraftClient.getInstance().getToastManager().add(new CustomImageToast(
                                MinecraftClient.getInstance().textRenderer,
                                Identifier.of("mineboxaddons", "textures/images/shops/" + shop + ".png"),
                                Text.of(getToastTitle(shop)),
                                Text.of(getToastDescription(shop))
                        ));

                        playXpSound();
                    });
                }
            } else {
                // Reset the flag when the shop status changes
                shopRefreshed.put(shop, false);
            }
        });
    }

    // Helper method to get toast titles
    private String getToastTitle(String shop) {
        return switch (shop) {
            case "cheese" -> "Cheese just restocked!";
            case "coffee" -> "Buckstar just restocked!";
            case "cocktail" -> "The bar just restocked!";
            case "bakery" -> "Bakery just restocked!";
            default -> "Shop just restocked!";
        };
    }

    // Helper method to get toast descriptions
    private String getToastDescription(String shop) {
        return switch (shop) {
            case "cheese" -> "Find it at the Italian Restaurant.";
            case "coffee" -> "Buy Coffee from Buckstar.";
            case "cocktail" -> "Visit the Cocktail Bar.";
            case "bakery" -> "Visit the French Bakery.";
            default -> "Visit the shop now.";
        };
    }

    public static void playXpSound() {
        // Get the instance of the Minecraft client
        MinecraftClient client = MinecraftClient.getInstance();

        // Get the sound manager from the client
        SoundManager soundManager = client.getSoundManager();

        // Play the XP sound
        soundManager.play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f));
    }
}