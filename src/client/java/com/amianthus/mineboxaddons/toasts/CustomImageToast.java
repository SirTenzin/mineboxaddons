/**
 * Sections of this code were taken from WildfireFemaleGenderMod accessible at:
 * https://github.com/WildfireRomeo/WildfireFemaleGenderMod/blob/fabric-1.21.4/src/main/java/com/wildfire/gui/WildfireToast.java
 * <p>
 * WildfireFemaleGenderMod is licensed under the GNU Lesser General Public License v3.0
 * Copyright (C) 2023-present WildfireRomeo
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.amianthus.mineboxaddons.toasts;

import com.amianthus.mineboxaddons.MineboxAddonsClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomImageToast implements Toast {
    private final Identifier iconTexture;
    private final List<OrderedText> text;
    private Visibility visibility = Visibility.SHOW;

    public CustomImageToast(TextRenderer textRenderer, Identifier iconTexture, Text title, Text description) {
        this.iconTexture = iconTexture;
        this.text = new ArrayList<>(2);
        this.text.addAll(textRenderer.wrapLines(title.copy().withColor(Color.YELLOW.getRGB()), 126));
        if (description != null) {
            this.text.addAll(textRenderer.wrapLines(description, 126));
        }
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        this.visibility = time > (MineboxAddonsClient.CONFIG.toastTime() * 1000L) ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getHeight() {
        return 7 + this.getTextHeight() + 3;
    }

    private int getTextHeight() {
        return Math.max(this.text.size(), 2) * 11;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        int i = this.getHeight();
        context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("toast/advancement"), 0, 0, this.getWidth(), i);

        context.drawTexture(RenderLayer::getGuiTextured, iconTexture, 6, 6, 0, 0, 20, 20, 20, 20, 20, 20);

        int j = this.text.size() * 11;
        int k = 7 + (this.getTextHeight() - j) / 2;
        for (int l = 0; l < this.text.size(); l++) {
            OrderedText line = this.text.get(l);
            int yPosition = k + l * 11;
            context.drawText(textRenderer, line, 30, yPosition, 0xFFFFFF, false);
        }
    }
}