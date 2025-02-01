package com.amianthus.mineboxaddons.mixin.client;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListHud.class)
public interface PlayerListHudMixin {
    @Accessor("footer")
    Text getFooter();
}
