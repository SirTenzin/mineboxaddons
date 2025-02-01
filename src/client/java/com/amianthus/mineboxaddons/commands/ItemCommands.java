package com.amianthus.mineboxaddons.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ItemCommands {
    public static int execute(CommandContext<FabricClientCommandSource> context, String itemName) {
        System.out.println("Command executed"); // Debug print 1
        context.getSource().getClient().execute(() -> {
            System.out.println("Inside execute"); // Debug print 2
            context.getSource().sendFeedback(Text.of("Opening screen..."));
            context.getSource().getClient().execute(() -> {
                System.out.println("Inside client execute"); // Debug print 4
            });
            System.out.println("After screen open"); // Debug print 3
        });
        return 1;
    }
}