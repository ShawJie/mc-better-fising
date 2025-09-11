package com.shawjie.mods.exception;

public class ConfigLoadFailedException extends BetterFishBaseException {

    public ConfigLoadFailedException(Throwable e) {
        super("Load better-fishing config file failed", e);
    }
}
