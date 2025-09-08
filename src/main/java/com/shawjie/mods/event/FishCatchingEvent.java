package com.shawjie.mods.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;

/**
 * Callback interface for handling fish catching events.
 * Implementations define specific actions to perform when a fish is caught.
 */
public interface FishCatchingEvent {

    Event<FishCatchingEvent> EVENT = EventFactory.createArrayBacked(
        FishCatchingEvent.class, (callbackActions) ->
            ((player, fishingBobberEntity) -> {
            for (FishCatchingEvent action : callbackActions) {
                action.whenFishCatching(player, fishingBobberEntity);
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
        PlayerEntity player,
        FishingBobberEntity fishingBobberEntity
    );
}
