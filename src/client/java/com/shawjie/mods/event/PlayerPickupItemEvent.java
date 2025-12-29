package com.shawjie.mods.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public interface PlayerPickupItemEvent {

    Event<PlayerPickupItemEvent> EVENT = EventFactory.createArrayBacked(PlayerPickupItemEvent.class,
            (listeners) -> (playerInventory, entity, amount) -> {
                for (PlayerPickupItemEvent event : listeners) {
                    event.interact(playerInventory, entity, amount);
                }
            }
    );

    void interact(Inventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp);
}