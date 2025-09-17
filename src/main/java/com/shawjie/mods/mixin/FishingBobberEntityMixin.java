package com.shawjie.mods.mixin;

import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Mixin for FishingBobberEntity to intercept fish catching events.
 * Uses @Inject to trigger callbacks when tracked data changes.
 *
 * @author shawjie
 */
@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

	private static final Integer CLEAR_DEPRECATED_TICK = 3;

	private static final Set<Integer> PROCESSED_EVENT_CACHE = new HashSet<>();

	@Shadow
	private boolean caughtFish;

	/**
	 * Injected method that triggers when fishing bobber tracked data changes.
	 * Executes registered callbacks when a fish is caught.
	 */
	@Inject(
		method = "onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V",
		at = @At("TAIL")
	)
	private void afterTrackedDataSet(TrackedData<?> data, CallbackInfo info) {
		FishingBobberEntity bobberEntity = ((FishingBobberEntity)((Object)this));
		if (bobberEntity == null || !this.caughtFish) {
			return;
		}

		int actionEntityId = bobberEntity.getId();
		if (!getEndableConfig() || !PROCESSED_EVENT_CACHE.add(actionEntityId)) {
			return;
		}
		PlayerEntity playerOwner = bobberEntity.getPlayerOwner();

		FishCatchingEvent invoker = FishCatchingEvent.EVENT.invoker();
		invoker.whenFishCatching(playerOwner, bobberEntity);

		PriorityFabricTicker.scheduleTask(() -> PROCESSED_EVENT_CACHE.remove(actionEntityId), CLEAR_DEPRECATED_TICK);
	}

	private boolean getEndableConfig() {
		return Optional.of(ConfigurationLoader.getInstance())
			.map(ConfigurationLoader::getConfig)
			.map(BetterFishingConfigurationProperties::getAutoFishingEnable)
			.orElse(Boolean.TRUE);
	}
}