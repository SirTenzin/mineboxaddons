package com.amianthus.mineboxaddons.widgets;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum HUDManager {
    INSTANCE;

    private final List<HUDWidget> widgets = new ArrayList<>();

    public void registerWidget(HUDWidget widget) {
        if (!widgets.contains(widget)) {
            widgets.add(widget);
        }
    }

    public void deRegisterWidget(HUDWidget widget) {
        widgets.remove(widget);
    }

    public HUDWidget getWidget(String identifier) {
        for (HUDWidget w : widgets) {
            System.out.println(w.IDENTIFIER);
            System.out.println(w.getClass());
            if(w.IDENTIFIER.equals(identifier)) { // Changed to use widget's own identifier
                return w;
            }
        }
        return null;
    }

    public <T> T getWidget(String identifier, Class<T> clazz) {
        for (HUDWidget w : widgets) {
            System.out.println(w.IDENTIFIER);
            System.out.println(w.getClass());
            if (w.IDENTIFIER.equals(identifier) && clazz.isInstance(w)) {
                return clazz.cast(w);
            }
        }
        return null;
    }

    public List<HUDWidget> getWidgets() {
        return Collections.unmodifiableList(widgets);
    }

    public void setEditMode(boolean editMode) {
        widgets.forEach(w -> w.setEditMode(editMode));
    }

    public void renderWidgets(DrawContext context, float delta) {
        widgets.stream()
                .filter(HUDWidget::isEnabled)
                .forEach(w -> w.render(context, delta));
    }
}