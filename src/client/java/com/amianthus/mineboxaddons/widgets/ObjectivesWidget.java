package com.amianthus.mineboxaddons.widgets;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

public class ObjectivesWidget extends HUDWidget {
    private static final int PADDING = 5;
    private static final int VERTICAL_SPACE = 4;
    private static final int HEADER_COLOR = 0xFFA500; // Orange color
    private static Text text;
    public int index = 0;
    private static String[] objectives;
    private static int BASE_WIDTH = 0;

    public static final String IDENTIFIER = "MineboxAddons:ObjectivesWidget";

    public ObjectivesWidget(String[] objectives) {
        super(IDENTIFIER);
        ObjectivesWidget.text = Text.of(objectives[0]);
        ObjectivesWidget.objectives = objectives;
        if(MineboxAddonsClient.CONFIG.objectiveWidget()) adjustSize(MinecraftClient.getInstance().textRenderer);
        this.canResize = false;
    }

    @Override
    public void renderContent(DrawContext context, float delta) {
        if(!MineboxAddonsClient.CONFIG.objectiveWidget()) return;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Create header text
        Text headerText = Text.literal("Objectives:")
                .setStyle(Style.EMPTY.withBold(true).withUnderline(true).withColor(HEADER_COLOR));

        List<OrderedText> wrappedObjective = textRenderer.wrapLines(text, Integer.MAX_VALUE); // No width restriction

        // Determine max line width
        int maxLineWidth = textRenderer.getWidth(headerText);
        for (OrderedText line : wrappedObjective) {
            maxLineWidth = Math.max(maxLineWidth, textRenderer.getWidth(line));
        }

        // Set dynamic width
        BASE_WIDTH = maxLineWidth + 2 * PADDING;

        // Calculate dynamic height
        int headerHeight = textRenderer.fontHeight;
        int textHeight = wrappedObjective.size() * textRenderer.fontHeight;
        int totalHeight = PADDING + headerHeight + VERTICAL_SPACE + textHeight + PADDING;

        // Set widget size correctly
        setSize(BASE_WIDTH, totalHeight);

        // Center widget on the left side, vertically
        Window window = MinecraftClient.getInstance().getWindow();
        int screenHeight = window.getScaledHeight();
        setPosition(0, (screenHeight - totalHeight) / 2);

        // Draw semi-transparent background
        context.fill(0, 0, BASE_WIDTH, totalHeight, 0x80000000);

        // Draw header
        int headerX = (BASE_WIDTH - textRenderer.getWidth(headerText)) / 2;
        context.drawText(textRenderer, headerText, headerX, PADDING, HEADER_COLOR, true);

        // Draw objective lines
        int yStart = PADDING + textRenderer.fontHeight + VERTICAL_SPACE;
        for (int i = 0; i < wrappedObjective.size(); i++) {
            OrderedText line = wrappedObjective.get(i);
            int lineWidth = textRenderer.getWidth(line);
            context.drawText(textRenderer, line, (BASE_WIDTH - lineWidth) / 2, yStart + (i * textRenderer.fontHeight), 0xFFFFFFFF, true);
        }
    }

    @Override
    public void setSize(int width, int height) {
        if(!MineboxAddonsClient.CONFIG.objectiveWidget()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();

        // Use dynamically calculated width and height
        this.width = BASE_WIDTH;
        this.height = height;

        if (window != null) {
            // Keep widget within screen bounds
            int screenHeight = window.getScaledHeight();
            this.height = Math.min(height, screenHeight - y);

            // Center vertically on the left side
            setPosition(0, (screenHeight - this.height) / 2);
        }
    }

    public int next() {
        index++;
        if (index >= 0 && index < objectives.length) {
            text = Text.of(objectives[index]);
            adjustSize(MinecraftClient.getInstance().textRenderer);
            return 0;
        } else {
            if(this.enabled) this.toggleEnabled();
            HUDManager.INSTANCE.deRegisterWidget(this);
            text = null;
            this.index = 0;
            objectives = null;
            return 1;
        }
    }

    private void adjustSize(TextRenderer textRenderer) {
        if(!MineboxAddonsClient.CONFIG.objectiveWidget()) return;
        // Calculate max width including header
        Text headerText = Text.literal("Objectives:")
                .setStyle(Style.EMPTY.withBold(true).withUnderline(true).withColor(HEADER_COLOR));

        List<OrderedText> wrappedObjective = textRenderer.wrapLines(text, Integer.MAX_VALUE); // No width restriction

        int maxLineWidth = textRenderer.getWidth(headerText);
        for (OrderedText line : wrappedObjective) {
            maxLineWidth = Math.max(maxLineWidth, textRenderer.getWidth(line));
        }

        // Update BASE_WIDTH dynamically
        BASE_WIDTH = maxLineWidth + 2 * PADDING;

        // Wrap text again with correct width
        wrappedObjective = textRenderer.wrapLines(text, BASE_WIDTH - 2 * PADDING);

        // Calculate required height
        int headerHeight = textRenderer.fontHeight;
        int objectiveHeight = wrappedObjective.size() * textRenderer.fontHeight;
        int totalHeight = PADDING * 2 + headerHeight + VERTICAL_SPACE + objectiveHeight;

        // Set final widget size
        setSize(BASE_WIDTH, totalHeight);
    }
}