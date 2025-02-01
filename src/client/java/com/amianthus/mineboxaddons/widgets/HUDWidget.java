package com.amianthus.mineboxaddons.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.apache.commons.lang3.tuple.Pair;

public abstract class HUDWidget {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean enabled = true;
    protected boolean editMode = false;
    protected float scale = 1.0f;
    protected boolean canResize = true;

    protected String IDENTIFIER = "MineboxAddons:HUDWidget";

    protected HUDWidget(String identifier) {
        this.IDENTIFIER = identifier;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isEnabled() { return enabled; }
    public float getScale() { return scale; }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() &&
                mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    public void render(DrawContext context, float delta) {
        if (enabled || editMode) {
            // Save the current transformation state
            context.getMatrices().push();

            // Apply position translation
            context.getMatrices().translate(x, y, 0);
            savePosToConfig(x, y);

            // Apply scaling
            context.getMatrices().scale(scale, scale, 1);

            // Draw translucent background (same in both modes)
            context.fill(0, 0, (int)(width/scale), (int)(height/scale), 0x80000000);

            // Render the content
            renderContent(context, delta);

            // Restore the transformation state
            context.getMatrices().pop();

            // Render edit controls on top if in edit mode
            if (editMode) {
                renderEditControls(context);
            }
        }
    }


    protected void renderEditControls(DrawContext context) {
        // Draw edit mode outline instead of background
        context.fill(getX(), getY(), getX() + getWidth(), getY() + 1, 0xFF404040); // Top
        context.fill(getX(), getY(), getX() + 1, getY() + getHeight(), 0xFF404040); // Left
        context.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), 0xFF404040); // Right
        context.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), 0xFF404040); // Bottom

        if(canResize) {
            // Resize handle with 'R' text
            int resizeX = getX() + getWidth() - 10;
            int resizeY = getY() + getHeight() - 10;
            context.fill(resizeX, resizeY, resizeX + 10, resizeY + 10, 0xFF00FF00);
            context.drawText(MinecraftClient.getInstance().textRenderer,
                    "R", resizeX + 2, resizeY + 1, 0xFFFFFFFF, true);
        }

        // Hide button
        int buttonColor = enabled ? 0xFF00FF00 : 0xFFFF0000;
        context.fill(getX(), getY(), getX() + 10, getY() + 10, buttonColor);
        context.drawText(MinecraftClient.getInstance().textRenderer,
                "H", getX() + 2, getY() + 1, 0xFFFFFFFF, true);
    }

    public void renderWidget(DrawContext context, float delta) {
        if (editMode) {
            renderEditControls(context); // Mouse coordinates handled in config screen
        }
        if (enabled || editMode) {
            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(scale, scale, 1);
            renderContent(context, delta);
            context.getMatrices().pop();
        }
    }

    protected abstract void renderContent(DrawContext context, float delta);

    public void setPosition(int x, int y) {
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();

        if (window == null) {
            // Default position if window not initialized
            this.x = x;
            this.y = y;
            return;
        }

        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();

        // Clamp position within screen bounds
        this.x = Math.max(0, Math.min(x, screenWidth - width));
        this.y = Math.max(0, Math.min(y, screenHeight - height));
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public void updateTime(String status) {}

    public void setPosFromConfig(Pair<Integer, Integer> location) {
        System.out.println("Read location as " + location.toString());
        if(location.getRight() == -1337 || location.getLeft() == -1337) {
            setPosition(0, 0);
        } else {
            setPosition(location.getRight(), location.getLeft());
        }
    }

    protected void savePosToConfig(int x, int y) {}
}