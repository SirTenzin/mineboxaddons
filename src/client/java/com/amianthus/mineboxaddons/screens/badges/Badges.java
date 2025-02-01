package com.amianthus.mineboxaddons.screens.badges;

import java.awt.*;
import java.util.Map;

public class Badges {

    public static final Map<String, Dimension> raritySizes = Map.of(
            "common", new Dimension(41, 9),
            "uncommon", new Dimension(53, 9),
            "rare", new Dimension(29, 9),
            "mythic", new Dimension(39, 9),
            "legendary", new Dimension(59, 9)
    );

}
