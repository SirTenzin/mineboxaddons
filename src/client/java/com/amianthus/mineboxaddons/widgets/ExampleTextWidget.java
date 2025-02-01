package com.amianthus.mineboxaddons.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

public class ExampleTextWidget extends HUDWidget {
    private static final int BASE_WIDTH = 80;
    private static final int BASE_HEIGHT = 20;

    public ExampleTextWidget() {
        super("MineboxAddons:ExampleTextWidget");
        setSize(BASE_WIDTH, BASE_HEIGHT);
        setPosition(20, 20);
    }

    @Override
    public void renderContent(DrawContext context, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Prepare text
        String text = "Scale: " + String.format("%.1f", getScale());
        int textWidth = textRenderer.getWidth(text);

        // Calculate centered position using scaled dimensions
        int x = ((int)(width/scale) - textWidth) / 2;
        int y = ((int)(height/scale) - textRenderer.fontHeight) / 2;

        // Draw text
        context.drawText(textRenderer, text, x, y, 0xFFFFFFFF, true);
    }

    @Override
    public void setSize(int width, int height) {
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();

        if (window == null) {
            // Use base dimensions and scale if window is not initialized
            this.width = BASE_WIDTH;
            this.height = BASE_HEIGHT;
            this.scale = 1.0f;
            return;
        }

        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();

        // Ensure size stays within screen bounds
        this.width = Math.min(width, screenWidth - x);
        this.height = Math.min(height, screenHeight - y);

        // Update scale while maintaining aspect ratio
        this.scale = Math.min(this.width / (float) BASE_WIDTH, this.height / (float) BASE_HEIGHT);
    }
}