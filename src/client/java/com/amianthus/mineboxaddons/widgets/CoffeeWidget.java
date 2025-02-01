package com.amianthus.mineboxaddons.widgets;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class CoffeeWidget extends HUDWidget {
    private static final int PADDING = 3;
    private String timeTo;
    private String currentText;
    private int baseWidth;
    private int baseHeight;
    protected boolean enabled = false;

    public static final String IDENTIFIER = "MineboxAddons:CoffeeWidget";

    public CoffeeWidget() {
        super(IDENTIFIER);
        if(MineboxAddonsClient.CONFIG.coffeeTimerX() == -1337 && MineboxAddonsClient.CONFIG.coffeeTimerY() == -1337) {
            setPosition(0, 0);
        } else {
            setPosition(MineboxAddonsClient.CONFIG.coffeeTimerY(), MineboxAddonsClient.CONFIG.coffeeTimerY());
        }
    }

    @Override
    protected void savePosToConfig(int xPos, int yPos) {
        MineboxAddonsClient.CONFIG.cheeseTimerX(xPos);
        MineboxAddonsClient.CONFIG.cheeseTimerY(yPos);
        MineboxAddonsClient.CONFIG.save();
    }

    @Override
    public void render(DrawContext context, float delta) {
        if(!MineboxAddonsClient.CONFIG.coffeeTimer()) return;
        // Update text and calculate base dimensions
        currentText = "Coffee in: " + timeTo + " minutes";
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Calculate base dimensions (text size + padding)
        int textWidth = textRenderer.getWidth(currentText);
        int textHeight = textRenderer.fontHeight;
        baseWidth = textWidth + 2 * PADDING;
        baseHeight = textHeight + 2 * PADDING;

        // Auto-size when not in edit mode
        if (!editMode) {
            super.setSize((int)(baseWidth * scale), (int)(baseHeight * scale));
        }

        super.render(context, delta);
        savePosToConfig(x, y);
    }

    @Override
    public void setSize(int width, int height) {
        if (editMode) {
            // Calculate new scale while maintaining aspect ratio
            float scaleX = (float) width / baseWidth;
            float scaleY = (float) height / baseHeight;
            scale = Math.min(scaleX, scaleY);

            // Apply calculated scale to both dimensions
            super.setSize((int)(baseWidth * scale), (int)(baseHeight * scale));
        } else {
            // Normal size setting when not editing
            super.setSize(width, height);
        }
    }

    @Override
    public void renderContent(DrawContext context, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Calculate centered position within base dimensions
        // Draw text at base size (scaling handled by widget transformation)
        context.drawText(textRenderer, currentText, PADDING, PADDING, 0xFFFFFFFF, true);
    }

    @Override
    public void updateTime(String time) {
        timeTo = time;
    }
}