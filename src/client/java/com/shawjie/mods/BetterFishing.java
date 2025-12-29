package com.shawjie.mods;

import com.shawjie.mods.action.ItemPickUpAndThrowAction;
import com.shawjie.mods.action.PullUpAndReleaseThenAction;
import com.shawjie.mods.infrastructure.ActionProcessRegister;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.EnableAction;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Better Fishing mod entry point.
 * Provides automatic fishing functionality with event-driven architecture.
 */
@EnableAction(
	classes = {
		PullUpAndReleaseThenAction.class,
		ItemPickUpAndThrowAction.class
	}
)
public class BetterFishing implements ModInitializer {

	public static final String MOD_ID = "better-fishing";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Ready to registry `{}` for your game", MOD_ID);
		ConfigurationLoader configurationLoader = ConfigurationLoader.getInstance();
		configurationLoader.doLoader();
		ActionProcessRegister actionProcessRegister = new ActionProcessRegister(BetterFishing.class);
		Event<ClientTickEvents.StartTick> startClientTick = ClientTickEvents.START_CLIENT_TICK;
		startClientTick.register(new PriorityFabricTicker());

		actionProcessRegister.dispatcherEventListener();
		LOGGER.info("Mod `{}` registry successful, enjoy your fishing time", MOD_ID);
	}
}