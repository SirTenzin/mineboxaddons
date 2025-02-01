package com.amianthus.mineboxaddons;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.text.Text;

public class CustomItemCategory implements DisplayCategory<CustomItemDisplay> {
    private final CategoryIdentifier<CustomItemDisplay> identifier;
    private final Text name;
    private final Renderer icon;

    public CustomItemCategory(CategoryIdentifier<CustomItemDisplay> identifier,
                              Text name, Renderer icon) {
        this.identifier = identifier;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public CategoryIdentifier<CustomItemDisplay> getCategoryIdentifier() {
        return identifier;
    }

    @Override
    public Text getTitle() {
        return name;
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth() {
        return 112; // Standard REI category width
    }

    @Override
    public int getDisplayHeight() {
        return 36; // Standard REI category height
    }
}