package com.shawjie.mods;

import com.shawjie.mods.action.DropCatchItemAction;
import com.shawjie.mods.action.FishCatchingCallbackAction;
import com.shawjie.mods.action.FishCatchingCallbackActionChain;
import com.shawjie.mods.action.PullUpAndReleaseThenAction;
import com.shawjie.mods.mixin.FishingBobberEntityMixin;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Better Fishing mod entry point.
 * Provides automatic fishing functionality with event-driven architecture.
 */
public class BetterFishing implements ModInitializer {
	public static final String MOD_ID = "better-fishing";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private final List<FishCatchingCallbackAction> callbackActionList = new ArrayList<>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Ready to registry `{}` for your game", MOD_ID);
		callbackActionList.add(new PullUpAndReleaseThenAction());
		callbackActionList.add(new DropCatchItemAction());

		Event<ClientTickEvents.StartTick> startClientTick = ClientTickEvents.START_CLIENT_TICK;
		startClientTick.register(new PriorityFabricTicker());

		FishCatchingCallbackActionChain callbackActionChain = FishCatchingCallbackActionChain.getInstance();
		for (FishCatchingCallbackAction action : callbackActionList) {
			callbackActionChain.registerCallback(action::processAction);
		}
		LOGGER.info("Mod `{}` registry successful, enjoy your fishing time", MOD_ID);
	}
}