package com.shawjie.mods.config;

import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration screen for Better Fishing mod in ModMenu
 */
public class BetterFishingConfigScreen extends Screen {
    
    private final Screen parent;
    private final ConfigurationLoader configLoader;
    
    // UI Components
    private CheckboxWidget autoFishingToggle;
    private CheckboxWidget stopBeforeRodBreakToggle;
    private TextFieldWidget pullUpTickField;
    private TextFieldWidget releaseTickField;
    private TextFieldWidget blockListField;
    
    // Temporary values for editing
    private boolean tempAutoFishingEnable;
    private boolean tempStopBeforeRodBreak;
    private int tempPullUpTick;
    private int tempReleaseTick;
    private String tempBlockListText;

    public BetterFishingConfigScreen(Screen parent, BetterFishingConfigurationProperties config, ConfigurationLoader configLoader) {
        super(Text.translatable("better-fishing.config.title"));
        this.parent = parent;
        this.configLoader = configLoader;
        
        // Initialize temporary values
        this.tempAutoFishingEnable = config.getAutoFishingEnable() != null ? config.getAutoFishingEnable() : true;
        this.tempStopBeforeRodBreak = config.getStopBeforeRodBreak() != null ? config.getStopBeforeRodBreak() : false;
        this.tempPullUpTick = config.getPullUpTick() != null ? config.getPullUpTick() : 5;
        this.tempReleaseTick = config.getReleaseTick() != null ? config.getReleaseTick() : 10;
        this.tempBlockListText = config.getBlockListItems() != null ? 
            String.join(", ", config.getBlockListItems()) : "";
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = 40;
        int spacing = 25;
        int labelWidth = 80;
        int fieldWidth = 80;
        
        // Auto Fishing Toggle
        this.autoFishingToggle = CheckboxWidget.builder(
            Text.translatable("better-fishing.config.auto_fishing_enable"), 
            this.textRenderer
        )
        .pos(centerX - 80, startY)
        .checked(tempAutoFishingEnable)
        .callback((checkbox, checked) -> tempAutoFishingEnable = checked)
        .build();
        this.addDrawableChild(autoFishingToggle);
        
        // Stop Before Rod Break Toggle
        this.stopBeforeRodBreakToggle = CheckboxWidget.builder(
            Text.translatable("better-fishing.config.stop_before_rod_break"), 
            this.textRenderer
        )
        .pos(centerX - 80, startY + spacing)
        .checked(tempStopBeforeRodBreak)
        .callback((checkbox, checked) -> tempStopBeforeRodBreak = checked)
        .build();
        this.addDrawableChild(stopBeforeRodBreakToggle);
        
        // Pull Up Tick Field
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.pull_up_label"), 
            button -> {}
        ).dimensions(centerX - labelWidth, startY + spacing * 2, labelWidth, 20).build());
        
        this.pullUpTickField = new TextFieldWidget(
            this.textRenderer, 
            centerX + 5, startY + spacing * 2, fieldWidth, 20, 
            Text.translatable("better-fishing.config.pull_up_tick")
        );
        this.pullUpTickField.setText(String.valueOf(tempPullUpTick));
        this.pullUpTickField.setChangedListener(text -> {
            try {
                tempPullUpTick = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // Keep previous value if invalid
            }
        });
        this.addDrawableChild(pullUpTickField);
        
        // Release Tick Field
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.release_label"), 
            button -> {}
        ).dimensions(centerX - labelWidth, startY + spacing * 3, labelWidth, 20).build());
        
        this.releaseTickField = new TextFieldWidget(
            this.textRenderer, 
            centerX + 5, startY + spacing * 3, fieldWidth, 20, 
            Text.translatable("better-fishing.config.release_tick")
        );
        this.releaseTickField.setText(String.valueOf(tempReleaseTick));
        this.releaseTickField.setChangedListener(text -> {
            try {
                tempReleaseTick = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // Keep previous value if invalid
            }
        });
        this.addDrawableChild(releaseTickField);
        
        // Block List Field
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.block_list_label"), 
            button -> {}
        ).dimensions(centerX - labelWidth, startY + spacing * 4, labelWidth, 20).build());
        
        this.blockListField = new TextFieldWidget(
            this.textRenderer, 
            centerX + 5, startY + spacing * 4, 150, 20, 
            Text.translatable("better-fishing.config.block_list")
        );
        this.blockListField.setText(tempBlockListText);
        this.blockListField.setChangedListener(text -> tempBlockListText = text);
        this.addDrawableChild(blockListField);
        
        // Calculate button positions to ensure they're visible
        int buttonY = Math.min(startY + spacing * 5 + 10, this.height - 60);
        
        // Save and Cancel buttons side by side
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.save"), 
            this::saveConfig
        ).dimensions(centerX - 105, buttonY, 100, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"), 
            button -> this.close()
        ).dimensions(centerX + 5, buttonY, 100, 20).build());
    }
    
    private void saveConfig(ButtonWidget button) {
        BetterFishingConfigurationProperties newProperties = new BetterFishingConfigurationProperties();
        // Update configuration with new values
        newProperties.setAutoFishingEnable(tempAutoFishingEnable);
        newProperties.setStopBeforeRodBreak(tempStopBeforeRodBreak);
        newProperties.setPullUpTick(Math.max(1, tempPullUpTick)); // Ensure positive value
        newProperties.setReleaseTick(Math.max(1, tempReleaseTick)); // Ensure positive value
        
        // Parse block list
        Set<String> blockList = new HashSet<>();
        if (!tempBlockListText.trim().isEmpty()) {
            String[] items = tempBlockListText.split(",");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    blockList.add(trimmed);
                }
            }
        }
        newProperties.setBlockListItems(blockList);
        
        // Save configuration
        configLoader.refreshConfig(newProperties);
        
        // Close screen
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render a simple background without blur to avoid the crash
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        
        // Draw title
        context.drawCenteredTextWithShadow(
            this.textRenderer, 
            this.title, 
            this.width / 2, 
            20, 
            0xFFFFFF
        );
        
        // Draw compact field descriptions
        int descY = 110;
        int descSpacing = 25;
        
        context.drawTextWithShadow(
            this.textRenderer,
            Text.translatable("better-fishing.config.pull_up_tick_desc"),
            this.width / 2 - 120,
            descY,
            0xAAAAAA
        );
        
        context.drawTextWithShadow(
            this.textRenderer,
            Text.translatable("better-fishing.config.release_tick_desc"),
            this.width / 2 - 120,
            descY + descSpacing,
            0xAAAAAA
        );
        
        context.drawTextWithShadow(
            this.textRenderer,
            Text.translatable("better-fishing.config.block_list_desc"),
            this.width / 2 - 120,
            descY + descSpacing * 2,
            0xAAAAAA
        );
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
