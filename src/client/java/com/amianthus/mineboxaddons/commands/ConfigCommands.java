package com.amianthus.mineboxaddons.commands;

import com.amianthus.mineboxaddons.screens.HUDConfigScreen;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class ConfigCommands {
    @SuppressWarnings("SameReturnValue")
    public static int openHudGui(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new HUDConfigScreen()));
        return 1;
    }
}
