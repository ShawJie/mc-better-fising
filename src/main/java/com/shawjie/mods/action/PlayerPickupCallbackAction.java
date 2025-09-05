package com.shawjie.mods.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface PlayerPickupCallbackAction extends CallbackAction {

    void processAction(
        PlayerEntity playerEntity,
        ItemStack itemStack
    );
}
