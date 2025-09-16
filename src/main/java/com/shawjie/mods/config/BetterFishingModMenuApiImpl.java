package com.shawjie.mods.config;

import com.shawjie.mods.infrastructure.ConfigurationLoader;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class BetterFishingModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parentScreen) {
        ConfigurationLoader configLoader = ConfigurationLoader.getInstance();
        BetterFishingConfigurationProperties config = configLoader.getConfig();
        
        return new BetterFishingConfigScreen(parentScreen, config, configLoader);
    }
}
