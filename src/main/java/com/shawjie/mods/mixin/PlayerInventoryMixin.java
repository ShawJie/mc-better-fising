package com.shawjie.mods.mixin;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.action.PlayerPickupCallbackActionChain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(
        method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
        at = @At("RETURN")
    )
    private void afterPlayerInventoryInsertStack(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        PlayerPickupCallbackActionChain actionChain = PlayerPickupCallbackActionChain.getInstance();
        if (actionChain.callbackChainEmpty()) {
            return;
        }

        Boolean insertStackSuccess = callbackInfoReturnable.getReturnValue();
        System.out.println("ret:" + insertStackSuccess);
        if (insertStackSuccess == null || !insertStackSuccess) {
            return;
        }

        PlayerInventory playerEntity = ((PlayerInventory)((Object)this));
        actionChain.processAction(playerEntity.player, itemStack);
    }
}
