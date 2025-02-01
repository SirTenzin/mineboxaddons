package com.amianthus.mineboxaddons.listeners;

import com.amianthus.mineboxaddons.widgets.HUDManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderListener {
    public static void renderWidgets(DrawContext context, RenderTickCounter tickDelta) {
        if (!MinecraftClient.getInstance().options.hudHidden) {
            HUDManager.INSTANCE.renderWidgets(context, tickDelta.getTickDelta(false));
        }
    }
}
