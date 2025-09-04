package com.shawjie.mods.mixin;

import com.shawjie.mods.BetterFishing;
import com.shawjie.mods.action.FishCatchingCallbackAction;
import com.shawjie.mods.action.FishCatchingCallbackActionChain;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Mixin for FishingBobberEntity to intercept fish catching events.
 * Uses @Inject to trigger callbacks when tracked data changes.
 *
 * @author shawjie
 */
@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

	private static final Integer CLEAR_DEPRECATED_TICK = 20;
	private static final Set<Integer> processedEventCache = new HashSet<>();

	/**
	 * Injected method that triggers when fishing bobber tracked data changes.
	 * Executes registered callbacks when a fish is caught.
	 */
	@Inject(
		method = "onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V",
		at = @At("TAIL")
	)
	private void afterTrackedDataSet(CallbackInfo info) {
		FishCatchingCallbackActionChain actionChain = FishCatchingCallbackActionChain.getInstance();
		if (actionChain.callbackChainEmpty()) {
			return;
		}

		FishingBobberEntity bobberEntity = ((FishingBobberEntity)((Object)this));
		FishingBobberEntityFieldMixin entityFieldMixin = (FishingBobberEntityFieldMixin) bobberEntity;
		if (bobberEntity == null || !entityFieldMixin.getCaughtFish()) {
			return;
		}

		int actionEntityId = bobberEntity.getId();
		if (processedEventCache.contains(actionEntityId)) {
			return;
		}

		PlayerEntity playerOwner = bobberEntity.getPlayerOwner();
		processedEventCache.add(actionEntityId);
		actionChain.processAction(playerOwner, bobberEntity);

		PriorityFabricTicker.scheduleTask(() -> processedEventCache.remove(actionEntityId), CLEAR_DEPRECATED_TICK);
	}
}