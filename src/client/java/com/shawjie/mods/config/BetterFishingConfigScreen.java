package com.shawjie.mods.config;

import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.*;

/**
 * Configuration screen for Better Fishing mod in ModMenu
 * Modernized with card-based layout and enhanced user experience
 * Extends OptionsSubScreen for built-in scrolling support
 */
public class BetterFishingConfigScreen extends OptionsSubScreen {

    private final ConfigurationLoader baseLoader;
    private BetterFishingConfigurationProperties tmpProp;

    public BetterFishingConfigScreen(Screen parent, BetterFishingConfigurationProperties base, ConfigurationLoader baseLoader) {
        super(parent, Minecraft.getInstance().options, Component.translatable("better-fishing.config.title"));
        this.baseLoader = baseLoader;

        // Initialize temporary values
        tmpProp = generateTempProperties(base);
    }

    @Override
    protected void addOptions() {
        if (this.list == null) {
            return;
        }

        HeaderAndFooterLayout displayLayout = this.layout;
        this.list.addHeader(Component.translatable("better-fishing.config.group.basic"));
        this.list.addSmall(createBasicSettingsOptions());

        this.list.addHeader(Component.translatable("better-fishing.config.group.timing"));
        createTimingSettingsPanel(displayLayout.getX(), displayLayout.getY(), displayLayout.getWidth())
            .forEach(this.list::addBig);

        this.list.addHeader(Component.translatable("better-fishing.config.group.filter"));
        this.list.addSmall(createFilterSettingsPanel());
    }

    /**
     * Creates the basic settings card panel
     */
    private List<AbstractWidget> createBasicSettingsOptions() {
        // Auto Fishing Toggle
        Checkbox autoFishingToggle = Checkbox.builder(
                Component.translatable("better-fishing.config.auto_fishing_enable"),
                this.getFont()
            )
            .selected(tmpProp.getAutoFishingEnable())
            .onValueChange((checkbox, checked) -> tmpProp.setAutoFishingEnable(checked))
            .tooltip(Tooltip.create(Component.translatable("better-fishing.config.tooltip.auto_fishing")))
            .build();

        // Stop Before Rod Break Toggle
        Checkbox stopBeforeRodBreakToggle = Checkbox.builder(
                Component.translatable("better-fishing.config.stop_before_rod_break"),
                this.getFont()
            )
            .selected(tmpProp.getStopBeforeRodBreak())
            .onValueChange((checkbox, checked) -> tmpProp.setStopBeforeRodBreak(checked))
            .tooltip(Tooltip.create(Component.translatable("better-fishing.config.tooltip.rod_protection")))
            .build();

        // Block Junks Toggle
        Checkbox blockJunksToggle = Checkbox.builder(
                Component.translatable("better-fishing.config.block_junks"),
                this.getFont()
            )
            .selected(tmpProp.getBlockJunks())
            .onValueChange((checkbox, checked) -> tmpProp.setBlockJunks(checked))
            .tooltip(Tooltip.create(Component.translatable("better-fishing.config.tooltip.block_junks")))
            .build();

        return Arrays.asList(autoFishingToggle, stopBeforeRodBreakToggle, blockJunksToggle);
    }

    /**
     * Creates the timing settings card panel
     *
     * @return
     */
    private List<OptionInstance<?>> createTimingSettingsPanel(int x, int y, int width) {
        // Pull Up Slider
        OptionInstance<Integer> pullUpSlider = new OptionInstance<>(
            "better-fishing.config.pull_up_label",
            (t) -> Tooltip.create(Component.translatable("better-fishing.config.tooltip.pull_up")),
            (component, integer) -> Component.literal("ticks: " + integer),
            new OptionInstance.IntRange(5, 20), tmpProp.getPullUpTick(), tmpProp::setPullUpTick
        );

        OptionInstance<Integer> releaseSlider = new OptionInstance<>(
            "better-fishing.config.release_label",
            (t) -> Tooltip.create(Component.translatable("better-fishing.config.tooltip.release")),
            (component, integer) -> Component.literal("ticks: " + integer),
            new OptionInstance.IntRange(10, 500), tmpProp.getReleaseTick(), tmpProp::setReleaseTick
        );

        return Arrays.asList(pullUpSlider, releaseSlider);
    }

    /**
     * Creates the filter settings card panel
     */
    private List<AbstractWidget> createFilterSettingsPanel() {
        // Block List Field
        StringWidget stringWidget = new StringWidget(
            Component.translatable("better-fishing.config.block_list_label").withColor(-4539718),
            this.font
        );

        EditBox blockListField = new EditBox(
            this.font, 0, 0,  155, 15,
            Component.translatable("better-fishing.config.block_list_label")
        );

        blockListField.setValue(collectionAsString(tmpProp.getBlockListItems()));
        blockListField.setResponder(text -> {
            Collection<String> strings = stringAsCollection(text);
            if (strings.isEmpty()) {
                tmpProp.setBlockListItems(null);
                return;
            }
            tmpProp.setBlockListItems(new HashSet<>(strings));
        });
        blockListField.setMaxLength(256);
        blockListField.setTooltip(Tooltip.create(Component.translatable("better-fishing.config.tooltip.block_list")));

        return Arrays.asList(stringWidget, blockListField);
    }

    private String collectionAsString(Collection<String> collection) {
        return String.join(", ", collection);
    }

    private Collection<String> stringAsCollection(String collections) {
        List<String> collectionList = new ArrayList<>();
        if (!collections.trim().isEmpty()) {
            String[] items = collections.split(",");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    collectionList.add(trimmed);
                }
            }
        }

        return collectionList;
    }

    @Override
    protected void addFooter() {
        LinearLayout linearLayout = this.layout.addToFooter(LinearLayout.horizontal()).spacing(8);
        linearLayout.defaultCellSetting().alignHorizontallyCenter();

        linearLayout.addChild(
            Button.builder(Component.translatable("better-fishing.config.save"), button -> {
                baseLoader.refreshConfig(tmpProp);
                this.onClose();
            }).build()
        );

        linearLayout.addChild(
            Button.builder(CommonComponents.GUI_CANCEL, button -> this.onClose()).build()
        );
    }

    @Override
    public void removed() {

    }

    private BetterFishingConfigurationProperties generateTempProperties(BetterFishingConfigurationProperties base) {
        BetterFishingConfigurationProperties defaultProperties = new BetterFishingConfigurationProperties();
        // Initialize temporary values
        defaultProperties.setAutoFishingEnable(base.getAutoFishingEnable() != null ? base.getAutoFishingEnable() : true);
        defaultProperties.setStopBeforeRodBreak(base.getStopBeforeRodBreak() != null ? base.getStopBeforeRodBreak() : false);
        defaultProperties.setBlockJunks(base.getBlockJunks() != null ? base.getBlockJunks() : false);

        defaultProperties.setPullUpTick(base.getPullUpTick() != null ? base.getPullUpTick() : 5);
        defaultProperties.setReleaseTick(base.getReleaseTick() != null ? base.getReleaseTick() : 10);
        defaultProperties.setBlockListItems(base.getBlockListItems() != null ? base.getBlockListItems() : Collections.emptySet());
        return defaultProperties;
    }
}
