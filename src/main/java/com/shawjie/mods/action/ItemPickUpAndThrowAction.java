package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.event.PlayerPickupItemEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.Ordered;
import com.shawjie.mods.mixin.LootTableEntryAccessor;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.fabricmc.fabric.mixin.loot.LootPoolAccessor;
import net.fabricmc.fabric.mixin.loot.LootTableAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.entity.projectile.FishingHook;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Ordered
public class ItemPickUpAndThrowAction implements FishCatchingEvent, PlayerPickupItemEvent, CallbackAction {

    private final AtomicBoolean pickUpCandidate = new AtomicBoolean();
    private final AtomicReference<Set<String>> fishingItemsRef = new AtomicReference<>();

    @Override
    public void whenFishCatching(Player player, FishingHook fishingBobberEntity) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }
        ReloadableRegistries.Lookup registrieyLookup = Optional.ofNullable(fishingBobberEntity.getEntityWorld())
            .map(World::getServer)
            .map(MinecraftServer::getReloadableRegistries)
            .orElse(null);
        if (registrieyLookup == null) {
            return;
        }

        LootTable lootTable = Optional.ofNullable(registrieyLookup.getLootTable(LootTables.FISHING_GAMEPLAY)).orElse(LootTable.EMPTY);
        Set<String> lootItemSet = getItemsFromLootTable(lootTable, registrieyLookup);

        incrementFishingCountRecord();
        fishingItemsRef.set(lootItemSet);
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

        String itemId = registryEntry.getIdAsString();
        boolean itemInLootList = Optional.ofNullable(fishingItemsRef.get()).orElseGet(Collections::emptySet).contains(itemId);
        boolean itemInBlock = blockItemsFromConfig().contains(itemId);
        if (!itemInLootList || !reduceFishingCountRecord() || !itemInBlock) {
            return;
        }

        ItemStack removeStack = playerPickingUpItems.removeStack(slot, entityBeingPickedUp.getCount());
        if (removeStack != ItemStack.EMPTY) {
            player.dropItem(removeStack, true, true);
        }
    }

    private void incrementFishingCountRecord() {
        pickUpCandidate.set(Boolean.TRUE);
        PriorityFabricTicker.scheduleTask(() -> pickUpCandidate.compareAndSet(Boolean.TRUE, Boolean.FALSE), 30);
    }

    private boolean reduceFishingCountRecord() {
        return pickUpCandidate.compareAndSet(Boolean.TRUE, Boolean.FALSE);
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

    public ItemPickUpAndThrowAction() {
    }
}
