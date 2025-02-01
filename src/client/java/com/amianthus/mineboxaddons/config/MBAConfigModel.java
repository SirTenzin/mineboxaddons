package com.amianthus.mineboxaddons.config;

import io.wispforest.owo.config.annotation.*;

@Modmenu(modId="mineboxaddons")
@Config(name = "mbaconfig", wrapperName = "MBAConfig")
public class MBAConfigModel {

    // Shop Widgets
    @SectionHeader("shopWidgetsHeader")

    public boolean bakeryTimer = false;
    @ExcludeFromScreen
    public int bakeryTimerX = -1337;
    @ExcludeFromScreen
    public int bakeryTimerY = -1337;

    public boolean cheeseTimer = false;
    @ExcludeFromScreen
    public int cheeseTimerX = -1337;
    @ExcludeFromScreen
    public int cheeseTimerY = -1337;

    public boolean cocktailTimer = false;
    @ExcludeFromScreen
    public int cocktailTimerX = -1337;
    @ExcludeFromScreen
    public int cocktailTimerY = -1337;

    public boolean coffeeTimer = false;
    @ExcludeFromScreen
    public int coffeeTimerX = -1337;
    @ExcludeFromScreen
    public int coffeeTimerY = -1337;

    // Durability Widgets
    @SectionHeader("durabilityWidgetsHeader")

    public boolean durabilityWidget = false;
    @ExcludeFromScreen
    public int durabilityWidgetX = -1337;
    @ExcludeFromScreen
    public int durabilityWidgetY = -1337;

    // Objective Widgets
    @SectionHeader("objectiveWidgetsHeader")

    public boolean objectiveWidget = true;
    @ExcludeFromScreen
    public int objectiveWidgetX = -1337;
    @ExcludeFromScreen
    public int objectiveWidgetY = -1337;

    // QoL
    @SectionHeader("qolHeader")

    public boolean autoIslandTp = false;
    @RangeConstraint(min = 1, max = 10)
    public int autoIslandTpDelay = 5;

    public boolean bankMacro = true;

    @SectionHeader("toastHeader")

    @RangeConstraint(min = 1, max = 10)
    public int toastTime = 5;

    public boolean showToasts = true;

    @SectionHeader("devOptionsHeader")

    @Nest
    public DevOptions devOptions = new DevOptions();

    public static class DevOptions {
        @RestartRequired
        public boolean devMode = false;
    }
}
