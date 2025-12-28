package com.shawjie.mods.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NestedLootTable.class)
public interface LootTableEntryAccessor {

    @Accessor("value")
    Either<ResourceKey<LootTable>, LootTable> getValue();
}
