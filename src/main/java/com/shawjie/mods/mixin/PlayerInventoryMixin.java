package com.shawjie.mods.mixin;

import com.shawjie.mods.event.PlayerPickupItemEvent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Inject(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At("TAIL"), cancellable = true)
    private void onItemPickup(int slot, ItemStack stack, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        PlayerPickupItemEvent.EVENT.invoker().interact((PlayerInventory) (Object) this, slot, stack);
    }
}