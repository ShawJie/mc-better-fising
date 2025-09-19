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
        
        // Responsive layout calculations
        int minWidth = 320; // Minimum supported width
        int actualWidth = Math.max(this.width, minWidth);
        int centerX = actualWidth / 2;
        
        // Dynamic spacing based on screen height
        int minHeight = 240; // Minimum supported height
        int actualHeight = Math.max(this.height, minHeight);
        int availableHeight = actualHeight - 80; // Reserve space for title and buttons
        int maxContentHeight = 150; // Maximum content area height
        int contentHeight = Math.min(availableHeight, maxContentHeight);
        
        // Calculate spacing to fit all elements
        int elementCount = 6; // 2 toggles + 3 input fields + 1 spacing
        int spacing = Math.max(18, Math.min(25, contentHeight / elementCount));
        
        int startY = 35;
        int labelWidth = Math.min(80, actualWidth / 4);
        int fieldWidth = Math.min(80, actualWidth / 5);
        
        // Ensure elements don't go off-screen
        int maxElementWidth = actualWidth - 40; // 20px margin on each side
        int toggleWidth = Math.min(160, maxElementWidth);
        
        int currentY = startY;
        
        // Auto Fishing Toggle
        this.autoFishingToggle = CheckboxWidget.builder(
            Text.translatable("better-fishing.config.auto_fishing_enable"), 
            this.textRenderer
        )
        .pos(centerX - toggleWidth / 2, currentY)
        .checked(tempAutoFishingEnable)
        .callback((checkbox, checked) -> tempAutoFishingEnable = checked)
        .build();
        this.addDrawableChild(autoFishingToggle);
        currentY += spacing;
        
        // Stop Before Rod Break Toggle
        this.stopBeforeRodBreakToggle = CheckboxWidget.builder(
            Text.translatable("better-fishing.config.stop_before_rod_break"), 
            this.textRenderer
        )
        .pos(centerX - toggleWidth / 2, currentY)
        .checked(tempStopBeforeRodBreak)
        .callback((checkbox, checked) -> tempStopBeforeRodBreak = checked)
        .build();
        this.addDrawableChild(stopBeforeRodBreakToggle);
        currentY += spacing;
        
        // Pull Up Tick Field
        int labelX = Math.max(20, centerX - labelWidth - 5);
        int fieldX = Math.min(actualWidth - fieldWidth - 20, centerX + 5);
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.pull_up_label"), 
            button -> {}
        ).dimensions(labelX, currentY, labelWidth, 20).build());
        
        this.pullUpTickField = new TextFieldWidget(
            this.textRenderer, 
            fieldX, currentY, fieldWidth, 20, 
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
        currentY += spacing;
        
        // Release Tick Field
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.release_label"), 
            button -> {}
        ).dimensions(labelX, currentY, labelWidth, 20).build());
        
        this.releaseTickField = new TextFieldWidget(
            this.textRenderer, 
            fieldX, currentY, fieldWidth, 20, 
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
        currentY += spacing;
        
        // Block List Field (wider field for better usability)
        int blockListFieldWidth = Math.min(150, actualWidth - labelWidth - 40);
        int blockListFieldX = Math.min(actualWidth - blockListFieldWidth - 20, centerX + 5);
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.block_list_label"), 
            button -> {}
        ).dimensions(labelX, currentY, labelWidth, 20).build());
        
        this.blockListField = new TextFieldWidget(
            this.textRenderer, 
            blockListFieldX, currentY, blockListFieldWidth, 20, 
            Text.translatable("better-fishing.config.block_list")
        );
        this.blockListField.setText(tempBlockListText);
        this.blockListField.setChangedListener(text -> tempBlockListText = text);
        this.addDrawableChild(blockListField);
        currentY += spacing + 10;
        
        // Calculate button positions to ensure they're always visible
        int buttonY = Math.max(currentY, actualHeight - 40);
        int buttonWidth = Math.min(100, (actualWidth - 30) / 2); // Ensure buttons fit side by side
        
        // Save and Cancel buttons side by side
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("better-fishing.config.save"), 
            this::saveConfig
        ).dimensions(centerX - buttonWidth - 5, buttonY, buttonWidth, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"), 
            button -> this.close()
        ).dimensions(centerX + 5, buttonY, buttonWidth, 20).build());
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
        
        // Only draw descriptions if there's enough space
        if (this.height > 200) {
            // Calculate responsive description positions
            int minWidth = 320;
            int actualWidth = Math.max(this.width, minWidth);
            int availableHeight = Math.max(this.height, 240) - 80;
            int maxContentHeight = 150;
            int contentHeight = Math.min(availableHeight, maxContentHeight);
            int elementCount = 6;
            int spacing = Math.max(18, Math.min(25, contentHeight / elementCount));
            
            // Start descriptions after the toggles (2 * spacing + startY)
            int descStartY = 35 + spacing * 2 + 5;
            int descX = Math.max(20, this.width / 2 - Math.min(120, actualWidth / 3));
            
            // Only show descriptions if they won't overlap with buttons
            int maxDescY = descStartY + spacing * 3;
            if (maxDescY < this.height - 60) {
                context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("better-fishing.config.pull_up_tick_desc"),
                    descX,
                    descStartY,
                    0xAAAAAA
                );
                
                context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("better-fishing.config.release_tick_desc"),
                    descX,
                    descStartY + spacing,
                    0xAAAAAA
                );
                
                context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("better-fishing.config.block_list_desc"),
                    descX,
                    descStartY + spacing * 2,
                    0xAAAAAA
                );
            }
        }
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
