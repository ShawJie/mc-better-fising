package com.shawjie.mods.action;

import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.infrastructure.Ordered;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import com.shawjie.mods.ticker.PriorityFabricTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Automatic fishing action that pulls up caught fish and casts a new line.
 * Implements randomized delays to simulate natural player behavior.
 */
@Ordered(Integer.MIN_VALUE)
public class PullUpAndReleaseThenAction implements FishCatchingEvent, CallbackAction {

    private final Integer SAVE_ROD_THRESHOLD = 5;
    private final Integer DEFAULT_DELAY_TICK = 5;
    private final Random DELAY_TICK_RANDOM = new Random();

    @Override
    public void whenFishCatching(Player player, FishingHook fishingHook) {
        // Find which hand is holding the fishing rod
        Optional<ItemStackWithHand> handThatHoldRod =
            Stream.of(
                new ItemStackWithHand(player.getMainHandItem(), InteractionHand.MAIN_HAND),
                new ItemStackWithHand(player.getOffhandItem(), InteractionHand.OFF_HAND)
            )
            .filter(stackHand -> holdingFishingRod(stackHand.itemStack()))
            .findFirst();

        if (handThatHoldRod.isEmpty()) {
            return;
        }

        final InteractionHand optHand = handThatHoldRod.get().operateHand();
        // Schedule the pull-up action with initial delay
        PriorityFabricTicker.scheduleTask(() -> {
            ItemStack stackInHand = player.getItemInHand(optHand);
            Minecraft catchClient = PriorityFabricTicker.getClient();

            if (holdingFishingRod(stackInHand) && catchClient.gameMode != null) {
                // Pull up the fishing line
                catchClient.gameMode.useItem(player, optHand);


                if (getConfigStopBeforeRodBreak()) {
                    ItemStack fishingRodItem = handThatHoldRod.get().itemStack();
                    if (fishingRodItem.getMaxDamage() - fishingRodItem.getDamageValue() <= SAVE_ROD_THRESHOLD) {
                        return;
                    }
                }

                // Schedule the cast action with randomized delay
                PriorityFabricTicker.scheduleTask(() -> {
                    Minecraft releaseClient = PriorityFabricTicker.getClient();
                    if (holdingFishingRod(stackInHand) && releaseClient.gameMode != null) {
                        // Cast a new fishing line
                        releaseClient.gameMode.useItem(player, optHand);
                    }
                }, getConfigReleaseTick());
            }
        }, getConfigPullupTick());

    }

    private boolean holdingFishingRod(ItemStack playerStackInHand) {
        return Items.FISHING_ROD.equals(playerStackInHand.getItem().asItem());
    }

    private Integer getConfigPullupTick() {
        return Optional.of(ConfigurationLoader.getInstance())
            .map(ConfigurationLoader::getConfig)
            .map(BetterFishingConfigurationProperties::getPullUpTick)
            .orElse(DEFAULT_DELAY_TICK);
    }

    private Integer getConfigReleaseTick() {
        return Optional.of(ConfigurationLoader.getInstance())
            .map(ConfigurationLoader::getConfig)
            .map(BetterFishingConfigurationProperties::getReleaseTick)
            .orElse(DEFAULT_DELAY_TICK) + DELAY_TICK_RANDOM.nextInt(DEFAULT_DELAY_TICK);
    }

    private Boolean getConfigStopBeforeRodBreak() {
        return Optional.of(ConfigurationLoader.getInstance())
            .map(ConfigurationLoader::getConfig)
            .map(BetterFishingConfigurationProperties::getStopBeforeRodBreak)
            .orElse(Boolean.FALSE);
    }

    private record ItemStackWithHand(ItemStack itemStack, InteractionHand operateHand){}
}
