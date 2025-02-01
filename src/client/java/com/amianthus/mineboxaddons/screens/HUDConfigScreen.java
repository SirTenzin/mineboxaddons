package com.amianthus.mineboxaddons.screens;

import com.amianthus.mineboxaddons.widgets.HUDManager;
import com.amianthus.mineboxaddons.widgets.HUDWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HUDConfigScreen extends Screen {
    private static final int GRID_SIZE = 10;
    private static final int SNAP_DISTANCE = 8;

    private HUDWidget selectedWidget;
    private boolean dragging;
    private boolean resizing;

    private static final int BACKGROUND_COLOR = 0x90202020; // Dark translucent background
    private static final int DISABLED_OVERLAY = 0x80000000; // 50% opacity black overlay

    public HUDConfigScreen() {
        super(Text.literal("HUD Configuration"));
        HUDManager.INSTANCE.setEditMode(true);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Do nothing to keep the background transparent
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Draw background
//        context.fill(0, 0, width, height, 0x00000000);
        // 2. Draw grid on top of background but under widgets
//        drawGrid(context);

        // 3. Render widgets with full opacity
        for (HUDWidget widget : HUDManager.INSTANCE.getWidgets()) {
            // Store original enabled state
            boolean wasEnabled = widget.isEnabled();

            // Force enable for rendering in config screen
            if(!widget.isEnabled()) {
                widget.toggleEnabled();
            }
            widget.render(context, delta);

            // Restore original state
            if(!wasEnabled) {
                widget.toggleEnabled();
            }

            // Draw disabled overlay if needed
            if (!widget.isEnabled()) {
                context.fill(
                        widget.getX(), widget.getY(),
                        widget.getX() + widget.getWidth(),
                        widget.getY() + widget.getHeight(),
                        DISABLED_OVERLAY
                );
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawGrid(DrawContext context) {
        int gridColor = 0x50FFFFFF;
        for (int x = 0; x < width; x += GRID_SIZE) {
            context.drawVerticalLine(x, 0, height, gridColor);
        }
        for (int y = 0; y < height; y += GRID_SIZE) {
            context.drawHorizontalLine(0, width, y, gridColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Iterate through widgets in reverse order to handle overlapping correctly
        List<HUDWidget> widgets = new ArrayList<>(HUDManager.INSTANCE.getWidgets());
        Collections.reverse(widgets);

        for (HUDWidget widget : widgets) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                // Check resize handle first
                if (mouseX >= widget.getX() + widget.getWidth() - 5 &&
                        mouseY >= widget.getY() + widget.getHeight() - 5 &&
                        mouseX <= widget.getX() + widget.getWidth() &&
                        mouseY <= widget.getY() + widget.getHeight()) {
                    resizing = true;
                    dragging = false;
                    selectedWidget = widget;
                    return true;
                }

                // Then check hide button
                if (mouseX >= widget.getX() && mouseX <= widget.getX() + 10 &&
                        mouseY >= widget.getY() && mouseY <= widget.getY() + 10) {
                    widget.toggleEnabled();
                    return true;
                }

                // Finally handle general dragging
                dragging = true;
                resizing = false;
                selectedWidget = widget;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button,
                                double deltaX, double deltaY) {
        if (selectedWidget != null) {
            if (dragging) {
                handleDragging(mouseX, mouseY);
            } else if (resizing) {
                handleResizing(mouseX, mouseY);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }


    // Modified handleDragging method with bounds checking
    private void handleDragging(double mouseX, double mouseY) {
        int newX = (int) mouseX - selectedWidget.getWidth() / 2;
        int newY = (int) mouseY - selectedWidget.getHeight() / 2;

        // Keep widget within screen bounds
        newX = MathHelper.clamp(newX, 0, width - selectedWidget.getWidth());
        newY = MathHelper.clamp(newY, 0, height - selectedWidget.getHeight());

        // Snap to grid
        newX = (newX / GRID_SIZE) * GRID_SIZE;
        newY = (newY / GRID_SIZE) * GRID_SIZE;

        // Snap to center
        if (Math.abs(newX + selectedWidget.getWidth()/2 - width/2) < SNAP_DISTANCE) {
            newX = width/2 - selectedWidget.getWidth()/2;
        }

        selectedWidget.setPosition(newX, newY);
    }

    // Modified handleResizing method with bounds checking
    private void handleResizing(double mouseX, double mouseY) {
        int newWidth = (int) (mouseX - selectedWidget.getX());
        int newHeight = (int) (mouseY - selectedWidget.getY());

        // Keep within screen bounds
        newWidth = MathHelper.clamp(newWidth, 20, width - selectedWidget.getX());
        newHeight = MathHelper.clamp(newHeight, 20, height - selectedWidget.getY());

        selectedWidget.setSize(newWidth, newHeight);
    }

    @Override
    public void close() {
        super.close();
        HUDManager.INSTANCE.setEditMode(false);
    }
}