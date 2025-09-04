package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FishCatchingCallbackActionChain implements FishCatchingCallbackAction {

    private static final FishCatchingCallbackActionChain INSTANCE = new FishCatchingCallbackActionChain();
    private final List<BiConsumer<PlayerEntity, FishingBobberEntity>> callbackChain = new ArrayList<>();

    public boolean callbackChainEmpty() {
        return callbackChain.isEmpty();
    }

    @Override
    public void processAction(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        for (BiConsumer<PlayerEntity, FishingBobberEntity> consumer : callbackChain) {
            try {
                consumer.accept(player, fishingBobberEntity);
            } catch (Exception e) {
                BetterFishing.LOGGER.error("Fishing bobber callback invoke error: ", e);
            }
        }
    }

    /**
     * Register a callback to be executed when a fish is caught.
     *
     * @param consumer the callback function to register
     */
    public void registerCallback(BiConsumer<PlayerEntity, FishingBobberEntity> consumer) {
        this.callbackChain.add(consumer);
    }

    public static FishCatchingCallbackActionChain getInstance() {
        return INSTANCE;
    }

    private FishCatchingCallbackActionChain(){}
}
