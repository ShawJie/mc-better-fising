package com.shawjie.mods.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public interface PlayerPickupItemEvent {

    Event<PlayerPickupItemEvent> EVENT = EventFactory.createArrayBacked(PlayerPickupItemEvent.class,
            (listeners) -> (player, entity, amount) -> {
                for (PlayerPickupItemEvent event : listeners) {
                    event.interact(player, entity, amount);
                }
            }
    );

    void interact(Inventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp);
}