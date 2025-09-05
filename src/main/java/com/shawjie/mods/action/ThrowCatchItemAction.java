package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.*;

public class ThrowCatchItemAction implements FishCatchingCallbackAction, PlayerPickupCallbackAction {

    private final Map<String, Integer> fishingCatchRecord = new HashMap<>();
    private final Set<Item> thrownItemConfig = new HashSet<>(Collections.singletonList(Items.COD));

    @Override
    public void processAction(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        fishingCatchRecord.compute(
            player.getUuidAsString(),
            (key, oldVal) -> (oldVal == null ? 0 : oldVal) + 1
        );
        BetterFishing.LOGGER.info("Catch log: {}", fishingCatchRecord);
    }

    @Override
    public void processAction(PlayerEntity playerEntity, ItemStack itemStack) {
        String playerUuid = playerEntity.getUuidAsString();
        BetterFishing.LOGGER.info("Pickup log: {}: {}", playerUuid, itemStack);
        if (!fishingCatchRecord.containsKey(playerUuid)) {
            return;
        }

        /*ItemStack stack = itemEntity.getStack();
        Item pickupedItem = stack.getItem().asItem();
        if (thrownItemConfig.contains(pickupedItem)) {
            PlayerInventory inventory = playerEntity.getInventory();
            int matchingSlot = inventory.getMatchingSlot(stack.getRegistryEntry(), stack);
            if (matchingSlot >= 0) {
                inventory.removeStack()
            }
        }*/

        Integer remain = fishingCatchRecord.computeIfPresent(playerUuid, (k, v) -> v - 1);
        if (remain != null && remain == 0) {
            fishingCatchRecord.remove(playerUuid);
        }
    }
}
