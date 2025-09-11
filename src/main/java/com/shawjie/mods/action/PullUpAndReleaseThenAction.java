package com.shawjie.mods.action;

import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.Ordered;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Automatic fishing action that pulls up caught fish and casts a new line.
 * Implements randomized delays to simulate natural player behavior.
 */
@Ordered(Integer.MIN_VALUE)
public class PullUpAndReleaseThenAction implements FishCatchingEvent, CallbackAction {

    private final Integer DEFAULT_DELAY_TICK = 5;
    private final Random DELAY_TICK_RANDOM = new Random();

    @Override
    public void whenFishCatching(PlayerEntity player, FishingBobberEntity fishingBobberEntity) {
        if (!getEndableConfig()) {
            return;
        }

        // Find which hand is holding the fishing rod
        Optional<Pair<ItemStack, Hand>> handThatHoldRod = Stream.of(
            new Pair<>(player.getMainHandStack(), Hand.MAIN_HAND),
            new Pair<>(player.getOffHandStack(), Hand.OFF_HAND)
        )
            .filter(handPair -> holdingFishingRod(handPair.getLeft()))
            .findFirst();

        if (handThatHoldRod.isEmpty()) {
            return;
        }

        final Hand optHand = handThatHoldRod.get().getRight();
        // Schedule the pull-up action with initial delay
        PriorityFabricTicker.scheduleTask(() -> {
            ItemStack stackInHand = player.getStackInHand(optHand);
            MinecraftClient catchClient = PriorityFabricTicker.getClient();

            if (holdingFishingRod(stackInHand) && catchClient.interactionManager != null) {
                // Pull up the fishing line
                catchClient.interactionManager.interactItem(player, optHand);

                // Schedule the cast action with randomized delay
                PriorityFabricTicker.scheduleTask(() -> {
                    MinecraftClient releaseClient = PriorityFabricTicker.getClient();
                    if (holdingFishingRod(stackInHand) && releaseClient.interactionManager != null) {
                        // Cast a new fishing line
                        releaseClient.interactionManager.interactItem(player, optHand);
                    }
                }, (DEFAULT_DELAY_TICK + DELAY_TICK_RANDOM.nextInt(DEFAULT_DELAY_TICK)));
            }
        }, DEFAULT_DELAY_TICK);

    }

    private boolean holdingFishingRod(ItemStack playerStackInHand) {
        return Items.FISHING_ROD.equals(playerStackInHand.getItem().asItem());
    }

    private boolean getEndableConfig() {
        return Optional.of(ConfigurationLoader.getInstance())
            .map(ConfigurationLoader::getConfig)
            .map(BetterFishingConfigurationProperties::getAutoFishingEnable)
            .orElse(Boolean.TRUE);
    }
}
