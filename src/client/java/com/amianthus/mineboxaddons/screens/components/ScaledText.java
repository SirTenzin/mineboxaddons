package com.amianthus.mineboxaddons.screens.components;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class ScaledText extends LabelComponent {

    private Text text;
    private final float scale;
    private final int color;

    public ScaledText(Text text, float scale, int color) {
        super(text);
        this.text = text;
        this.scale = scale;
        this.color = color;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float v, float v1) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Save the current transformation matrix
        context.getMatrices().push();

        // Apply scaling and translation
        context.getMatrices().translate(this.x, this.y, 0);
        context.getMatrices().scale(this.scale, this.scale, 1);

//        this.sizing(Sizing.fixed((int) (textRenderer.getWidth(this.text) * this.scale)), Sizing.fixed((int) (textRenderer.fontHeight * this.scale)));
        // Draw the scaled text
        context.drawText(textRenderer, this.text, 0, 0, this.color, true);
        this.margins(Insets.of(0, (int) (textRenderer.fontHeight * this.scale) + 5, 0, 0));
        // Restore the transformation matrix
        context.getMatrices().pop();
    }

    public void setText(Text text) {
        this.text = text;
    }
}