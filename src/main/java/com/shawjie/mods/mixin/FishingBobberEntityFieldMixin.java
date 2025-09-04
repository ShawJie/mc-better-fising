package com.shawjie.mods.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

@Mixin(FishingBobberEntity.class)
public interface FishingBobberEntityFieldMixin {

	@Accessor
	boolean getCaughtFish();

}