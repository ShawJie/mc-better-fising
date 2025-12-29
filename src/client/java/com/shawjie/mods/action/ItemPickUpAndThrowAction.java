package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.event.PlayerPickupItemEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.Ordered;
import com.shawjie.mods.mixin.NestedLootTableAccessor;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.fabricmc.fabric.mixin.loot.LootPoolAccessor;
import net.fabricmc.fabric.mixin.loot.LootTableAccessor;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Ordered
public class ItemPickUpAndThrowAction implements FishCatchingEvent, PlayerPickupItemEvent, CallbackAction {

    private final AtomicBoolean pickUpCandidate = new AtomicBoolean();
    private final AtomicReference<Set<String>> fishingItemsRef = new AtomicReference<>();

    @Override
    public void whenFishCatching(Player player, FishingHook fishingHook) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ReloadableServerRegistries.Holder registryHolder = Optional.of(fishingHook.level())
            .map(Level::getServer)
            .map(MinecraftServer::reloadableRegistries)
            .orElse(null);
        if (registryHolder == null) {
            return;
        }

        LootTable lootTable = Optional.of(registryHolder.getLootTable(BuiltInLootTables.FISHING)).orElse(LootTable.EMPTY);
        Set<String> lootItemSet = getItemsFromLootTable(lootTable, registryHolder);

        incrementFishingCountRecord();
        fishingItemsRef.set(lootItemSet);
    }

    @Override
    public void interact(Inventory playerPickingUpItems, int slot, ItemStack entityBeingPickedUp) {
        Player player = playerPickingUpItems.player;
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        UUID uuid = player.getUUID();
        BetterFishing.LOGGER.info("Player {} picked item: {}", uuid, entityBeingPickedUp);
        Holder<Item> registryEntry = entityBeingPickedUp.getItemHolder();

        String itemId = registryEntry.getRegisteredName();
        boolean itemInLootList = Optional.ofNullable(fishingItemsRef.get()).orElseGet(Collections::emptySet).contains(itemId);
        boolean itemInBlock = blockItemsFromConfig().contains(itemId);
        if (!itemInLootList || !reduceFishingCountRecord() || !itemInBlock) {
            return;
        }

        ItemStack removeStack = playerPickingUpItems.removeItem(slot, entityBeingPickedUp.getCount());
        if (removeStack != ItemStack.EMPTY) {
            player.drop(removeStack, true, true);
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

    private Set<String> getItemsFromLootTable(LootTable lootTable, ReloadableServerRegistries.Holder registryHolder) {
        Set<String> lootItems = new HashSet<>();

        List<LootPool> pools = ((LootTableAccessor) lootTable).fabric_getPools();
        for (LootPool pool : pools) {
            LootPoolAccessor poolAccessor = (LootPoolAccessor) pool;
            List<LootPoolEntryContainer> lootPoolEntries = poolAccessor.fabric_getEntries();
            for (LootPoolEntryContainer container : lootPoolEntries) {
                if (container.getType() == LootPoolEntries.LOOT_TABLE) {
                    NestedLootTableAccessor lootTableEntry = (NestedLootTableAccessor) container;
                    lootItems.addAll(
                        getItemsFromLootTable(
                            lootTableEntry.getContents().map(registryHolder::getLootTable, Function.identity()),
                            registryHolder
                        )
                    );
                }
                if (container.getType() == LootPoolEntries.ITEM) {
                    LootItem itemEntry = (LootItem) container;
                    itemEntry.createItemStack((itemStack) ->
                        lootItems.add(itemStack.getItemHolder().getRegisteredName()), null
                    );
                }
            }
        }
        return lootItems;
    }

    public ItemPickUpAndThrowAction() {
    }
}
