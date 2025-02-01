package com.amianthus.mineboxaddons.commands;

import com.amianthus.mineboxaddons.ArgumentTypes.MarketDirection;
import com.amianthus.mineboxaddons.ArgumentTypes.MarketDirectionArgumentType;
import com.amianthus.mineboxaddons.ArgumentTypes.Period;
import com.amianthus.mineboxaddons.ArgumentTypes.PeriodArgumentType;
import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.Items;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.Item;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.MarketData;
import com.amianthus.mineboxaddons.screens.ViewItemScreen;
import com.amianthus.mineboxaddons.utils.ChatUtils;
import com.google.gson.JsonArray;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class BazaarCommands {
    public static int price(CommandContext<FabricClientCommandSource> context) {
        Period period = PeriodArgumentType.getPeriod(context, "period");
        MarketDirection direction = MarketDirectionArgumentType.getDirection(context, "direction");
        ClientPlayerEntity player = context.getSource().getPlayer();
        String itemName;
        String periodString = period.name().toLowerCase();
        boolean isBuying = direction == MarketDirection.BUY;

        if (player.getMainHandStack().isEmpty())
            itemName = StringArgumentType.getString(context, "item name");
        else itemName = player.getMainHandStack().getItem().getName().getString();
        if(itemName == null || itemName.isEmpty()) {
            Text errorMessage = ChatUtils.createPrefixedText("Please hold an item in your hand or specify a name.", true);
            context.getSource().sendFeedback(errorMessage);
            return 0;
        }

        MineboxAddonsClient.LOGGER.info("Getting market data for {} for the most recent {} to {}.", itemName, periodString, isBuying ? "buy" : "sell");

        CompletableFuture.runAsync(() -> {
            ImmutablePair<String, MarketData> marketResponse = new Items().getItemPriceByName(itemName, periodString);
            String marketName = marketResponse.getLeft();
            MarketData marketData = marketResponse.getRight();

            if (marketData != null) {
                if(marketData.getBuyEntries() == null || marketData.getSellEntries() == null) {
                    MinecraftClient.getInstance().execute(() -> {
                        Text errorMessage = ChatUtils.createPrefixedText("Could not find market data for '" + itemName + "' for the most recent " + periodString + ".", true);
                        context.getSource().sendFeedback(errorMessage);
                    });
                    return;
                }
                MinecraftClient.getInstance().execute(() -> {
                    List<MarketData.PriceEntry> entries = isBuying ? marketData.getBuyEntries() : marketData.getSellEntries();
                    String directionText = isBuying ? "Buy" : "Sell";

                    switch (periodString) {
                        default: {
                            marketData.getNewestDay(entries).ifPresentOrElse(valueData -> {
                                String formattedDate = valueData.getFormattedDate();
                                String message = "Market data for " + marketName + " for the most recent day:\n" + directionText + ": " + valueData.value() + " on " + formattedDate;
                                context.getSource().sendFeedback(ChatUtils.createMultiLinePrefixedText(message));
                            }, () -> {
                                String message = "Could not find market data for " + marketName + " for the most recent day.";
                                context.getSource().sendFeedback(ChatUtils.createMultiLinePrefixedText(message));
                            });
                            break;
                        }

                        case "week", "month": {
                            List<MarketData.PriceEntry> periodEntries = periodString.equals("week") ? marketData.getNewestWeek(entries) : marketData.getNewestMonth(entries);
                            if (!periodEntries.isEmpty()) {
                                periodEntries = marketData.flattenEntries(periodEntries);
                                StringBuilder message = new StringBuilder("Market data for " + directionText + "ing " + marketName + " for the most recent " + periodString + ":\n");
                                for (MarketData.PriceEntry entry : periodEntries) {
                                    message.append("[").append(entry.getFormattedDate()).append("]: ").append(Math.round(entry.value())).append("\n");
                                }
                                message.append(String.format(
                                        "The %s closed at %s lux on %s",
                                        periodString,
                                        Math.round(periodEntries.getFirst().value()),
                                        periodEntries.getFirst().getFormattedDate()
                                ));
                                context.getSource().sendFeedback(ChatUtils.createMultiLinePrefixedText(message.toString()));
                            } else {
                                String message = "Could not find market data for " + directionText + "ing " + marketName + " for the most recent " + periodString + ".";
                                context.getSource().sendFeedback(ChatUtils.createMultiLinePrefixedText(message));
                            }
                            break;
                        }
                    }
                });
            } else {
                MinecraftClient.getInstance().execute(() -> {
                    Text errorMessage = ChatUtils.createPrefixedText("Could not find market data for '" + itemName + "' for the most recent " + periodString + ".", true);
                    context.getSource().sendFeedback(errorMessage);
                });
            }
        });

        return 1; // Return success
    }

    public static int view(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        AtomicReference<Item> finalItem = new AtomicReference<>();
        CompletableFuture.runAsync(() -> {
            String itemName;
            if (player.getMainHandStack().isEmpty())
                itemName = StringArgumentType.getString(context, "item name");
            else itemName = player.getMainHandStack().getItem().getName().getString();
            if(itemName == null || itemName.isEmpty()) {
                Text errorMessage = ChatUtils.createPrefixedText("Please hold an item in your hand or specify a name.", true);
                context.getSource().sendFeedback(errorMessage);
                return;
            }
            Item item = new Items().searchItemByName(itemName);
            if(item == null) {
                Text errorMessage = ChatUtils.createPrefixedText("Could not find item with name '" + itemName + "'.", true);
                context.getSource().sendFeedback(errorMessage);
                return;
            }
            finalItem.set(new Items().getItemByID(item.id()));
            if(finalItem.get() == null) return;
            context.getSource().getClient().send(() -> context.getSource().getClient().setScreen(new ViewItemScreen(finalItem.get())));
        });
        return 1;
    }

    public static int searchItem(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            String itemName = StringArgumentType.getString(context, "item name");

            // Run the search asynchronously
            CompletableFuture.runAsync(() -> {
                Items items = new Items();
                Item partialItem = items.searchItemByName(itemName);
                StringBuilder messageBuilder;
                if (partialItem != null) {
                    Formatting color = ChatUtils.RARITY_MAP.get(partialItem.rarity());
                    messageBuilder = new StringBuilder(partialItem.name() + "\n"
                            + partialItem.category() + " | " + partialItem.rarity() + " | Level " + partialItem.level() + "\n"
                            + partialItem.lore() + " " + partialItem.description());

                    for (Map.Entry<String, JsonArray> stat : partialItem.stats().entrySet()) {
                        if(stat.getValue().get(0) == stat.getValue().get(1))
                            messageBuilder.append("\n").append(stat.getKey()).append(": ").append(stat.getValue().get(0));
                        else messageBuilder.append("\n").append(stat.getKey()).append(": ").append(stat.getValue().get(0)).append(" - ").append(stat.getValue().get(1));
                    }

                    for (Map.Entry<String, JsonArray> damage : partialItem.damages().entrySet()) {
                        if(damage.getValue().get(0) == damage.getValue().get(1))
                            messageBuilder.append("\n").append(damage.getKey()).append(": ").append(damage.getValue().get(0));
                        else messageBuilder.append("\n").append(damage.getKey()).append(": ").append(damage.getValue().get(0)).append(" - ").append(damage.getValue().get(1));
                    }

                    // Send feedback back to the player on the main thread
                    MinecraftClient.getInstance().execute(() -> {
                        Text gradientText = ChatUtils.createMultiLinePrefixedText(messageBuilder.toString());
                        context.getSource().sendFeedback(gradientText);
                    });
                } else {
                    messageBuilder = new StringBuilder("Item not found");
                    // Send feedback back to the player on the main thread
                    MinecraftClient.getInstance().execute(() -> {
                        Text gradientText = ChatUtils.createMultiLinePrefixedText(messageBuilder.toString(), true);
                        context.getSource().sendFeedback(gradientText);
                    });
                }
            });

        }
        return 1;
    }
}
