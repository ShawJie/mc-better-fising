package com.shawjie.mods.exception;

public class BetterFishBaseException extends RuntimeException {

    public BetterFishBaseException(String message) {
        super(message);
    }

    public BetterFishBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
