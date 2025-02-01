package com.amianthus.mineboxaddons.listeners;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.Items;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.MarketData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ItemTooltipListener {
    private static final HashMap<String, Pair<String, String>> sellCache = MineboxAddonsClient.sellCache;
    private static final HashMap<String, Pair<String, String>> buyCache = MineboxAddonsClient.buyCache;
    private static final HashMap<String, Boolean> didRequest = MineboxAddonsClient.tooltipRequestDeduperCache;

    public static void injectTooltip(ItemStack stack, Item.TooltipContext context, TooltipType tooltipType, List<Text> tooltip) {
        String tooltipText = tooltip.stream().map(Text::getString).reduce("", String::concat);
        if (!tooltipText.contains("Lvl.") && !tooltipText.contains("Niv.")) return;
        String itemName = tooltip.getFirst().getString();
        Pair<String, String> emptyPair = Pair.of("No data", "No data");
        Pair<String, String> blockedPair = Pair.of("Blocked", "Blocked");
        if(itemName == null || itemName.isEmpty()) return;
        String firstLetter = itemName.charAt(0) + "";
        if(!firstLetter.toLowerCase().matches("[a-z]")) return;

        // Check if we have cached data
        boolean hasCachedData = sellCache.containsKey(itemName) && buyCache.containsKey(itemName);
        boolean hasBlockedData = Objects.equals(sellCache.getOrDefault(itemName, emptyPair).getRight(), "Blocked")
                || Objects.equals(buyCache.getOrDefault(itemName, emptyPair).getRight(), "Blocked");

        if (hasCachedData) {
            // Use cached data
            addTooltipText(tooltip, buyCache.get(itemName), sellCache.get(itemName));
            return;
        }

        if(hasBlockedData) {
            return;
        }

        // Show loading message only if we haven't requested yet
        if (!didRequest.containsKey(itemName) || !didRequest.get(itemName)) {
            // Add translatable "Fetching prices..." text
            Text fetchingText = Text.translatable("text.tooltips.fetching").formatted(Formatting.GRAY);
            tooltip.add(fetchingText);

            // Mark as requested
            didRequest.put(itemName, true);

            CompletableFuture.runAsync(() -> {
                try {
                    MarketData marketData = new Items().getItemPriceByName(itemName, "day").getRight();
                    Pair<String, String> buyPrice = marketData.getLatestBuyPrice();
                    Pair<String, String> sellPrice = marketData.getLatestSellPrice();

                    MinecraftClient.getInstance().execute(() -> {
                        // Remove "Fetching prices..." by comparing the translated string
                        String fetchingString = Text.translatable("text.tooltips.fetching").getString();
                        tooltip.removeIf(text -> text.getString().equals(fetchingString));

                        // Update caches and add price data
                        if (buyPrice != null) buyCache.put(itemName, buyPrice);
                        if (sellPrice != null) sellCache.put(itemName, sellPrice);
                        addTooltipText(tooltip, buyPrice, sellPrice);
                    });
                } catch (Exception e) {
                    MineboxAddonsClient.LOGGER.error("Failed to fetch price data for {}\n{}\n{}", itemName, e, Arrays.toString(e.getStackTrace()));
                    sellCache.put(itemName, blockedPair);
                    buyCache.put(itemName, blockedPair);
                }
            });
        } else {
            // Add translatable "Loading..." text
            Text loadingText = Text.translatable("text.tooltips.loading").formatted(Formatting.GRAY);
            tooltip.add(loadingText);
        }
    }

    private static void addTooltipText(List<Text> tooltip, Pair<String, String> buyPrice, Pair<String, String> sellPrice) {
        if (buyPrice != null) {
            String buyDate = buyPrice.getLeft();
            String buyPriceValue = buyPrice.getRight();
            Text buyText = Text.translatable("text.tooltips.buyprice")
                    .append(Text.literal("[" + buyDate + "] "))
                    .append(Text.literal(String.valueOf(buyPriceValue)))
                    .formatted(Formatting.GOLD);
            tooltip.add(buyText);
        }
        if (sellPrice != null) {
            String sellDate = sellPrice.getLeft();
            String sellPriceValue = sellPrice.getRight();
            Text sellText = Text.translatable("text.tooltips.sellprice")
                    .append(Text.literal("[" + sellDate + "] "))
                    .append(Text.literal(String.valueOf(sellPriceValue)))
                    .formatted(Formatting.GOLD);
            tooltip.add(sellText);
        }
    }
}