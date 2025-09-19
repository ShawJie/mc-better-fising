package com.shawjie.mods.infrastructure;

import com.google.gson.Gson;
import com.shawjie.mods.exception.ConfigLoadFailedException;
import com.shawjie.mods.property.BetterFishingConfigurationProperties;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ConfigurationLoader {

    private static final ConfigurationLoader INSTANCE = new ConfigurationLoader();

    private static final String BETTER_FISHING_CONFIG_FILE = "better-fishing.json";
    private BetterFishingConfigurationProperties properties;
    private final Gson gson;

    public BetterFishingConfigurationProperties doLoader() {
        FabricLoader instance = FabricLoader.getInstance();
        Path configFilePath = instance.getConfigDir().resolve(BETTER_FISHING_CONFIG_FILE);
        if (Files.notExists(configFilePath)) {
            configPersistent(configFilePath, generateDefaultConfig());
        }

        this.properties = loadConfigFromPersistent(configFilePath);
        return properties;
    }

    public static ConfigurationLoader getInstance() {
        return INSTANCE;
    }

    public BetterFishingConfigurationProperties getConfig() {
        return this.properties;
    }

    private void configPersistent(Path configFilePath, BetterFishingConfigurationProperties properties) {
        String jsonStr = gson.toJson(properties);
        try {
            Files.writeString(configFilePath, jsonStr, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ConfigLoadFailedException(e);
        }
    }

    private BetterFishingConfigurationProperties loadConfigFromPersistent(Path configFilePath) {
        String configString = null;
        try {
            configString = Files.readString(configFilePath);
        } catch (IOException e) {
            throw new ConfigLoadFailedException(e);
        }
        return gson.fromJson(configString, BetterFishingConfigurationProperties.class);
    }

    public void refreshConfig(BetterFishingConfigurationProperties properties) {
        FabricLoader instance = FabricLoader.getInstance();
        Path configFilePath = instance.getConfigDir().resolve(BETTER_FISHING_CONFIG_FILE);

        validateConfig(properties);
        configPersistent(configFilePath, properties);
        this.properties = properties;
    }

    private void validateConfig(BetterFishingConfigurationProperties properties) {
        if (properties.getPullUpTick() == null || properties.getPullUpTick() < 5) {
            properties.setPullUpTick(5);
        }
        if (properties.getPullUpTick() > 20) {
            properties.setPullUpTick(20);
        }
        if (properties.getReleaseTick() == null || properties.getReleaseTick() < 10) {
            properties.setReleaseTick(10);
        }
        if (properties.getReleaseTick() > 500) {
            properties.setReleaseTick(500);
        }
    }

    private BetterFishingConfigurationProperties generateDefaultConfig() {
        BetterFishingConfigurationProperties defaultConfigProperties = new BetterFishingConfigurationProperties();
        defaultConfigProperties.setAutoFishingEnable(true);
        defaultConfigProperties.setPullUpTick(5);
        defaultConfigProperties.setReleaseTick(10);
        defaultConfigProperties.setBlockListItems(Collections.emptySet());
        defaultConfigProperties.setStopBeforeRodBreak(Boolean.FALSE);
        return defaultConfigProperties;
    }

    private ConfigurationLoader() {
        this.gson = new Gson();
    }
}
