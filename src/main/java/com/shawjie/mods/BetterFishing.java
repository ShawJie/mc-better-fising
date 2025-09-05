package com.shawjie.mods;

import com.shawjie.mods.action.*;
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

	private final List<CallbackAction> callbackActionList = new ArrayList<>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Ready to registry `{}` for your game", MOD_ID);

		callbackActionList.add(new PullUpAndReleaseThenAction());
		callbackActionList.add(new ThrowCatchItemAction());

		Event<ClientTickEvents.StartTick> startClientTick = ClientTickEvents.START_CLIENT_TICK;
		startClientTick.register(new PriorityFabricTicker());

		prepareFishCatchingCallback();
		preparePlayerPickupCallback();

		LOGGER.info("Mod `{}` registry successful, enjoy your fishing time", MOD_ID);
	}

	private void prepareFishCatchingCallback() {
		FishCatchingCallbackActionChain callbackActionChain = FishCatchingCallbackActionChain.getInstance();
		for (CallbackAction action : callbackActionList) {
			if (action instanceof FishCatchingCallbackAction) {
				callbackActionChain.registerCallback((FishCatchingCallbackAction) action);
			}
		}
	}

	private void preparePlayerPickupCallback() {
		PlayerPickupCallbackActionChain callbackActionChain = PlayerPickupCallbackActionChain.getInstance();
		for (CallbackAction action : callbackActionList) {
			if (action instanceof PlayerPickupCallbackAction) {
				callbackActionChain.registerCallback((PlayerPickupCallbackAction) action);
			}
		}
	}
}