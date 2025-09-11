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
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

@Ordered
public class ItemPickUpAndThrowAction implements FishCatchingEvent, PlayerPickupItemEvent, CallbackAction {

    private final Map<UUID, Integer> fishingCountRecord = new HashMap<>();

    @Override
    public void whenFishCatching(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }
        incrementFishingCountRecord(player);
    }

    @Override
    public void interact(PlayerInventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp) {
        PlayerEntity player = playerPickingUpItems.player;
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }

        UUID uuid = player.getUuid();
        BetterFishing.LOGGER.info("Player {} picked item: {}", uuid, entityBeingPickedUp);
        RegistryEntry<Item> registryEntry = entityBeingPickedUp.getRegistryEntry();

        boolean itemInBlock = blockItemsFromConfig().contains(registryEntry.getIdAsString());
        if (reduceFishingCountRecord(player) == null || !itemInBlock) {
            return;
        }

        ItemStack removeStack = playerPickingUpItems.removeStack(slot, entityBeingPickedUp.getCount());
        if (removeStack != ItemStack.EMPTY) {
            player.dropItem(removeStack, false, true);
        }
    }

    private void incrementFishingCountRecord(PlayerEntity player) {
        fishingCountRecord.compute(player.getUuid(),
            (k, v) -> ((v == null) ? 0 : v) + 1);
    }

    private Integer reduceFishingCountRecord(PlayerEntity player) {
        UUID identify = player.getUuid();
        Integer remain = fishingCountRecord.computeIfPresent(identify, (k, v) -> v - 1);
        if (remain != null && remain == 0) {
            fishingCountRecord.remove(identify);
        }
        return remain;
    }

    private Set<String> blockItemsFromConfig() {
        return Optional.of(ConfigurationLoader.getInstance())
            .map(ConfigurationLoader::getConfig)
            .map(BetterFishingConfigurationProperties::getBlockListItems)
            .orElse(Collections.emptySet());
    }

    public ItemPickUpAndThrowAction() {
    }
}
