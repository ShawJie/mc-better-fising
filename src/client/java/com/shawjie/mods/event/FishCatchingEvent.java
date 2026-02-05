package com.shawjie.mods.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

import java.util.Objects;

/**
 * Callback interface for handling fish catching events.
 * Implementations define specific actions to perform when a fish is caught.
 */
public interface FishCatchingEvent {

    Event<FishCatchingEvent> EVENT = EventFactory.createArrayBacked(
        FishCatchingEvent.class, (callbackActions) ->
            ((player, fishingHook) -> {
                Minecraft client = Minecraft.getInstance();
                if (client.player == null || !Objects.equals(client.player, player)) {
                    return;
                }
                for (FishCatchingEvent action : callbackActions) {
                    action.whenFishCatching(player, fishingHook);
                }
        })
    );

    /**
     * Process the action when a fish is caught.
     * 
     * @param player the player who owns the fishing bobber
     * @param fishingBobberEntity the fishing bobber entity that caught the fish
     */
    void whenFishCatching(
        Player player,
        FishingHook fishingBobberEntity
    );
}
