package com.amianthus.mineboxaddons;

import com.amianthus.mineboxaddons.ArgumentTypes.ArgumentTypes;
import com.amianthus.mineboxaddons.config.MBAConfig;
import com.amianthus.mineboxaddons.listeners.EndTickListener;
import com.amianthus.mineboxaddons.listeners.HudRenderListener;
import com.amianthus.mineboxaddons.listeners.ItemTooltipListener;
import com.amianthus.mineboxaddons.listeners.MineboxJoinListener;
import com.amianthus.mineboxaddons.widgets.HUDManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MineboxAddonsClient implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("mineboxaddons");
	public static final HashMap<String, Pair<String, String>> sellCache = new HashMap<>();
	public static final HashMap<String, Pair<String, String>> buyCache = new HashMap<>();
	public static final HashMap<String, Boolean> tooltipRequestDeduperCache = new HashMap<>();
	public static final HUDManager hudManager = HUDManager.INSTANCE;
	public static boolean hasJoined = false;
	public static boolean hasRejoined = false;
	public static int tickDelay = 0;
	public static final MBAConfig CONFIG = MBAConfig.createAndLoad();

	@Override
	public void onInitializeClient() {
		ArgumentTypes.register();
		ClientCommands.register();
		ItemTooltipCallback.EVENT.register(ItemTooltipListener::injectTooltip);
		ClientTickEvents.END_CLIENT_TICK.register(new EndTickListener());
		HudRenderCallback.EVENT.register(HudRenderListener::renderWidgets);
		ClientPlayConnectionEvents.JOIN.register(MineboxJoinListener::onJoin);
		ClientTickEvents.END_CLIENT_TICK.register(MineboxJoinListener::autoTeleport);
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> CONFIG.save());
	}
}