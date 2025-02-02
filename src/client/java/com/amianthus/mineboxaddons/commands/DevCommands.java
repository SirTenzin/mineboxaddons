package com.amianthus.mineboxaddons.commands;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.Items;
import com.amianthus.mineboxaddons.mineboxapi.bazaar.structures.Item;
import com.amianthus.mineboxaddons.mixin.client.PlayerListHudMixin;
import com.amianthus.mineboxaddons.screens.ViewItemScreen;
import com.amianthus.mineboxaddons.utils.ChatUtils;
import com.amianthus.mineboxaddons.utils.ShopTimer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import okhttp3.OkHttpClient;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@SuppressWarnings("SameReturnValue")
public class DevCommands {
    public static int debugItem(CommandContext<FabricClientCommandSource> context) {
        ItemStack stack = context.getSource().getPlayer().getMainHandStack();
        if (stack.isEmpty() || !stack.isDamageable()) {
            Text message = ChatUtils.createPrefixedText("Item is not damageable.", true);
            context.getSource().sendFeedback(message);
            return 0;
        }

        // Get the maximum durability of the item
        int maxDurability = stack.getMaxDamage();

        // Get the current damage value
        int currentDamage = stack.getDamage();
        Text message = ChatUtils.createPrefixedText("Durability: " + (maxDurability - currentDamage) + "/" + maxDurability, true);
        context.getSource().sendFeedback(message);
        return 1;
    }

    public static int wikiItemWithID(CommandContext<FabricClientCommandSource> context) {
        String itemId = StringArgumentType.getString(context, "item id");
        CompletableFuture.runAsync(() -> {
            Item item = new Items().getItemByID(itemId);
            MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new ViewItemScreen(item)));
        });
        return 1;
    }

    public static int wikiItemWithName(CommandContext<FabricClientCommandSource> context) {
        String itemName = StringArgumentType.getString(context, "item name");
        CompletableFuture.runAsync(() -> {
            Item partialItem = new Items().searchItemByName(itemName);
            if(partialItem.id() != null) {
                Item fullItem = new Items().getItemByID(partialItem.id());
                MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new ViewItemScreen(fullItem)));
            } else {
                context.getSource().sendFeedback(ChatUtils.createPrefixedText("Item not found.", true));
            }
        });
        return 1;
    }

    public static int time(CommandContext<FabricClientCommandSource> context) {
        // Get the current Minecraft world
        World world = MinecraftClient.getInstance().world;
        if(world == null) {
            context.getSource().sendFeedback(ChatUtils.createPrefixedText("You must be in a world to use this command.", true));
            return 1;
        }
        // Get the time in ticks (ticks are 1/20th of a second)
        long time = getTime(world);
        long currentTimeMillis = System.currentTimeMillis();
        int currentMinute = Instant.now().atZone(ZoneOffset.UTC).getMinute();
        context.getSource().sendFeedback(ChatUtils.createPrefixedText("Time: " + formatTime(time)));
        context.getSource().sendFeedback(ChatUtils.createPrefixedText("System time: " + currentMinute));
        context.getSource().sendFeedback(ChatUtils.createPrefixedText(String.valueOf((currentTimeMillis / 60000) % 60)));
        StringBuilder shops = new StringBuilder();
        for (ShopTimer.Shop shop : ShopTimer.Shop.values()) {
            shops.append(
                    String.valueOf(
                            shop.name().toLowerCase().charAt(0)).toUpperCase())
                    .append(
                            shop.name().toLowerCase().substring(1))
                    .append(" opens in: ").append(ShopTimer.getShopStatus(shop, currentTimeMillis)).append(" minutes.").append("\n");
        }
        context.getSource().sendFeedback(ChatUtils.createMultiLinePrefixedText(shops.toString()));
        return 1;
    }

    // Helper method to format the time into Minecraft hours and minutes
    private static String formatTime(long time) {
        long totalTicks = time % 24000; // Ensure it wraps within a single Minecraft day (0 to 23999 ticks)
        totalTicks = (totalTicks + 6000) % 24000; // offset by 6000 ticks (6 AM) to start at 0
        int hours = (int)(totalTicks / 1000);  // 1000 ticks = 1 hour
        int minutes = (int)((totalTicks % 1000) / 16.666); // 1000 ticks divided by 60 minutes (1000 / 60 ≈ 16.666)
        return hours + ":" + (minutes < 10 ? "0" + minutes : minutes);
    }

    public static long getTime(World world) {
        // Get the current time of day (in ticks)
        return world.getTimeOfDay();
    }

    public static int serverInfo(CommandContext<FabricClientCommandSource> ctx) {
        ServerInfo info = MinecraftClient.getInstance().getCurrentServerEntry();
        World world = MinecraftClient.getInstance().world;
        PlayerListHud playerList = MinecraftClient.getInstance().inGameHud.getPlayerListHud();
        // Get the footer text
        PlayerListHudMixin playerListHudAccessor = (PlayerListHudMixin) playerList;
        Text footerText = playerListHudAccessor.getFooter();
        if(info != null) {
            String message = "Server Info: " + info.name + "\n" +
                    "Server IP: " + info.address + "\n" +
                    "Server Version: " + info.version + "\n" +
                    "Server MOTD: " + info.label + "\n" +
                    "Server Ping: " + info.ping + "ms" + "\n" +
                    "Server Label: " + info.label + "\n"
                    + "Footer: " + footerText.getString();
            String[] split = footerText.getString().split(Pattern.quote(" | "));
            if(split.length > 2) {
                message += "Server Instance: " + split[2].trim();
            }
            ctx.getSource().sendFeedback(ChatUtils.createMultiLinePrefixedText(message));
        }
        return 1;
    }

    public static int findClasses(CommandContext<FabricClientCommandSource> ctx) {
        MineboxAddonsClient.LOGGER.info("OkHttp class loader: {}", OkHttpClient.class.getClassLoader());
        MineboxAddonsClient.LOGGER.info("Classpath: {}", System.getProperty("java.class.path"));
        return 1;
    }
}
