package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.event.PlayerPickupItemEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;

public class ItemPickUpAndThrowAction implements FishCatchingEvent, PlayerPickupItemEvent, CallbackAction {

    @Override
    public void whenFishCatching(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {

    }

    @Override
    public void interact(PlayerInventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp) {
        BetterFishing.LOGGER.info("picked item: {}", entityBeingPickedUp);
    }
}
