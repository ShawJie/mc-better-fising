package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.event.PlayerPickupItemEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.Ordered;
import com.shawjie.mods.mixin.LootTableEntryAccessor;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import net.fabricmc.fabric.mixin.loot.LootPoolAccessor;
import net.fabricmc.fabric.mixin.loot.LootTableAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;

@Ordered
public class ItemPickUpAndThrowAction implements FishCatchingEvent, PlayerPickupItemEvent, CallbackAction {

    @Override
    public void whenFishCatching(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        ReloadableRegistries.Lookup registrieyLookup = Optional.ofNullable(fishingBobberEntity.getWorld())
            .map(World::getServer)
            .map(MinecraftServer::getReloadableRegistries)
            .orElse(null);
        if (registrieyLookup == null) {
            return;
        }

        LootTable lootTable = Optional.ofNullable(registrieyLookup.getLootTable(LootTables.FISHING_GAMEPLAY))
            .orElse(LootTable.EMPTY);
        Set<String> lootItemSet = getItemsFromLootTable(lootTable, registrieyLookup);
        BetterFishing.LOGGER.info("Show loot item: {}", lootItemSet);
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

    @SuppressWarnings("UnstableApiUsage")
    private Set<String> getItemsFromLootTable(LootTable lootTable, ReloadableRegistries.Lookup registrieyLookup) {
        Set<String> lootItems = new HashSet<>();

        List<LootPool> pools = ((LootTableAccessor) lootTable).fabric_getPools();
        for (LootPool pool : pools) {
            LootPoolAccessor poolAccessor = (LootPoolAccessor) pool;
            List<LootPoolEntry> lootPoolEntries = poolAccessor.fabric_getEntries();
            for (LootPoolEntry next : lootPoolEntries) {
                if (next.getType() == LootPoolEntryTypes.LOOT_TABLE) {
                    LootTableEntryAccessor lootTableEntry = (LootTableEntryAccessor) next;
                    lootItems.addAll(
                        getItemsFromLootTable(
                            lootTableEntry.getValue().map(registrieyLookup::getLootTable, Function.identity()),
                            registrieyLookup
                        )
                    );
                } else if (next.getType() == LootPoolEntryTypes.ITEM) {
                    ItemEntry itemEntry = (ItemEntry) next;
                    itemEntry.generateLoot((itemStack) ->
                        lootItems.add(itemStack.getRegistryEntry().getIdAsString()), null
                    );
                }
            }
        }
        return lootItems;
    }
}
