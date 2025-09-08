package com.shawjie.mods.event;

import com.shawjie.mods.action.CallbackAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;

/**
 * Callback interface for handling fish catching events.
 * Implementations define specific actions to perform when a fish is caught.
 */
public interface FishCatchingCallbackAction extends CallbackAction {

    /**
     * Process the action when a fish is caught.
     * 
     * @param player the player who owns the fishing bobber
     * @param fishingBobberEntity the fishing bobber entity that caught the fish
     */
    void processAction(
        PlayerEntity player,
        FishingBobberEntity fishingBobberEntity
    );
}
