package com.amianthus.mineboxaddons.widgets;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import com.amianthus.mineboxaddons.utils.DurabilityManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.tuple.Pair;

public class DurabilityWidget extends HUDWidget {
    private static final int PADDING = 3;
    private Pair<Number, Number> durability;
    private String currentText;
    private int baseWidth;
    private int baseHeight;

    public static final String IDENTIFIER = "MineboxAddons:DurabilityWidget";

    public DurabilityWidget() {
        super(IDENTIFIER);
        if(MineboxAddonsClient.CONFIG.durabilityWidgetX() == -1337 && MineboxAddonsClient.CONFIG.durabilityWidgetY() == -1337) {
            setPosition(0, 0);
        } else {
            setPosition(MineboxAddonsClient.CONFIG.durabilityWidgetX(), MineboxAddonsClient.CONFIG.durabilityWidgetY());
        }
    }

    @Override
    protected void savePosToConfig(int xPos, int yPos) {
        MineboxAddonsClient.CONFIG.durabilityWidgetX(xPos);
        MineboxAddonsClient.CONFIG.durabilityWidgetY(yPos);
        MineboxAddonsClient.CONFIG.save();
    }

    @Override
    public void render(DrawContext context, float delta) {
        if(!MineboxAddonsClient.CONFIG.durabilityWidget()) return;
        // Update text and calculate base dimensions
        durability = DurabilityManager.getInstance().getDurability();
        currentText = "Durability: " + durability.getLeft() + "/" + durability.getRight();
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

    public void updateDurability() {
        durability = DurabilityManager.getInstance().getDurability();
    }
}