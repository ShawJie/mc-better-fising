package com.shawjie.mods.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;

public class FishCatchingCallbackActionChain extends AbstractCallbackActionChain<FishCatchingCallbackAction>
    implements FishCatchingCallbackAction {

    private static final FishCatchingCallbackActionChain INSTANCE = new FishCatchingCallbackActionChain();

    @Override
    public void processAction(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        super.doProcessAction(c -> c.processAction(player, fishingBobberEntity));
    }

    public static FishCatchingCallbackActionChain getInstance() {
        return INSTANCE;
    }

    private FishCatchingCallbackActionChain(){}
}
