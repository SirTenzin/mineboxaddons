package com.amianthus.mineboxaddons.listeners;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import static com.amianthus.mineboxaddons.MineboxAddonsClient.*;

public class MineboxJoinListener {
    public static void onJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if(client.getCurrentServerEntry() != null && CONFIG.autoIslandTp()) {
            if(client.getCurrentServerEntry().address.equals("play.minebox.co")) {
                hasJoined = true; // Set a flag to indicate the player has joined Minebox
            }
        }
        tickDelay = 0; // Reset the tick delay counter
    }

    public static void autoTeleport(MinecraftClient client) {
        if(CONFIG.autoIslandTp() && hasJoined && !hasRejoined) {
            if (client.player != null && client.world != null) {
                tickDelay++;

                // Wait for a few ticks to ensure the world is fully loaded
                if (tickDelay >= 20 * CONFIG.autoIslandTpDelay()) { // 20 ticks = 1 second, wait for 5 seconds
                    hasJoined = false; // Reset the flag
                    // Check if the server is "play.minebox.co"
                    if (!client.isInSingleplayer() &&
                            client.getCurrentServerEntry() != null &&
                            "play.minebox.co".equals(client.getCurrentServerEntry().address)) {
                        // Execute the "/is" command
                        client.player.networkHandler.sendChatCommand("is");
                        hasRejoined = true;
                    }
                }
            }
        }
    }

}
