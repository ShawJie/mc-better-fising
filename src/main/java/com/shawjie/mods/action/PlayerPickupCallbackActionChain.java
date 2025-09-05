package com.shawjie.mods.action;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerPickupCallbackActionChain
    extends AbstractCallbackActionChain<PlayerPickupCallbackAction>
    implements PlayerPickupCallbackAction {

    private static final PlayerPickupCallbackActionChain INSTANCE = new PlayerPickupCallbackActionChain();

    @Override
    public void processAction(PlayerEntity playerEntity, ItemStack itemStack) {
        doProcessAction(c -> c.processAction(playerEntity, itemStack));
    }

    public static PlayerPickupCallbackActionChain getInstance() {
        return INSTANCE;
    }

    private PlayerPickupCallbackActionChain(){
        registerCallback(new ThrowCatchItemAction());
    }

}
