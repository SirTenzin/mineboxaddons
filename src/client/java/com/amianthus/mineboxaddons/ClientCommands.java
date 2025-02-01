package com.amianthus.mineboxaddons;

import com.amianthus.mineboxaddons.ArgumentTypes.MarketDirectionArgumentType;
import com.amianthus.mineboxaddons.ArgumentTypes.PeriodArgumentType;
import com.amianthus.mineboxaddons.commands.BazaarCommands;
import com.amianthus.mineboxaddons.commands.ConfigCommands;
import com.amianthus.mineboxaddons.commands.DevCommands;
import com.amianthus.mineboxaddons.commands.ObjectiveCommands;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT;


public class ClientCommands {
    public static void register() {
        EVENT.register(((dispatcher, registryAccess) -> {
            // /mba objective create <objectives>
            dispatcher.register(ClientCommandManager.literal("mba").then(ClientCommandManager.literal("objective").then(ClientCommandManager.literal("create")
                            .then(ClientCommandManager.argument("objectives", StringArgumentType.greedyString())
                                            .executes(ObjectiveCommands::createObjective)))));}));

        // /mba objective next
        EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("mba").then(ClientCommandManager.literal("objective").then(
                        ClientCommandManager.literal("next").executes(ObjectiveCommands::nextObjective))))));

        // /mbaq <objectives>
        EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("mbaq")
                .then(ClientCommandManager.argument("objectives", StringArgumentType.greedyString())
                        .executes(ObjectiveCommands::createObjective)))));

        // /mbaqn
        EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("mbaqn").executes(ObjectiveCommands::nextObjective))));

        if(MineboxAddonsClient.CONFIG.devOptions.devMode()) {
            // debugItem
            EVENT.register((dispatcher, registryAccess) ->
                    dispatcher.register(ClientCommandManager.literal("debugItem").executes(DevCommands::debugItem)));
            // /time
            EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("time").executes(DevCommands::time)));
            // /serverInfo
            EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("serverInfo").executes(DevCommands::serverInfo)));
        }

        // /mba config gui
        EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("mba").then(ClientCommandManager.literal("config")
                .then(ClientCommandManager.literal("gui")
                                .executes(ConfigCommands::openHudGui)))));

        // /items view <item name>
        EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("items").then(ClientCommandManager.literal("view")
                .then(ClientCommandManager.argument("item name", StringArgumentType.greedyString()).executes(BazaarCommands::view)))));

        // /items price <period> <direction> <item name>
        EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("items")
                        .then(ClientCommandManager.literal("price")
                                .then(ClientCommandManager.argument("period", new PeriodArgumentType())
                                        .then(ClientCommandManager.argument("direction", new MarketDirectionArgumentType())
                                                .then(ClientCommandManager.argument("item name", StringArgumentType.greedyString())
                                                        .executes(BazaarCommands::price)).executes(BazaarCommands::price))))));

        // /items search <item name>
        EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("items")
                .then(ClientCommandManager.literal("search").then(ClientCommandManager.argument("item name", StringArgumentType.greedyString())
                        .executes(BazaarCommands::searchItem)))));
    }
}
