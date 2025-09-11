package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.event.PlayerPickupItemEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.Ordered;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Ordered
public class ItemPickUpAndThrowAction implements FishCatchingEvent, PlayerPickupItemEvent, CallbackAction {

    @Override
    public void whenFishCatching(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        
    }

    @Override
    public void interact(PlayerInventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp) {
        BetterFishing.LOGGER.info("picked item: {}", entityBeingPickedUp);
        RegistryEntry<Item> registryEntry = entityBeingPickedUp.getRegistryEntry();

        boolean itemInBlock = blockItemsFromConfig().contains(registryEntry.getIdAsString());
        if (!itemInBlock) {
            return;
        }

        PlayerEntity targetPlayer = playerPickingUpItems.player;
        targetPlayer.dropItem(entityBeingPickedUp, false);
    }

    private Set<String> blockItemsFromConfig() {
        return Optional.of(ConfigurationLoader.getInstance())
            .map(ConfigurationLoader::getConfig)
            .map(BetterFishingConfigurationProperties::getBlockListItems)
            .orElse(Collections.emptySet());
    }
}
