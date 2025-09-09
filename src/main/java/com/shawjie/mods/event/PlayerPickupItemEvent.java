package com.shawjie.mods.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public interface PlayerPickupItemEvent {

    Event<PlayerPickupItemEvent> EVENT = EventFactory.createArrayBacked(PlayerPickupItemEvent.class,
            (listeners) -> (player, entity, amount) -> {
                for (PlayerPickupItemEvent event : listeners) {
                    event.interact(player, entity, amount);
                }
            }
    );

    void interact(PlayerInventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp);
}