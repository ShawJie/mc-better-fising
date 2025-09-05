package com.shawjie.mods.action;

import com.shawjie.mods.BetterFishing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCallbackActionChain<T extends CallbackAction> {

    private final List<T> callbackChain = new ArrayList<>();

    /**
     * Register a callback to be executed when a fish is caught.
     *
     * @param consumer the callback function to register
     */
    public void registerCallback(T consumer) {
        this.callbackChain.add(consumer);
    }

    public boolean callbackChainEmpty() {
        return callbackChain.isEmpty();
    }

    protected void doProcessAction(Consumer<T> actor) {
        for (T consumer : callbackChain) {
            try {
                actor.accept(consumer);
            } catch (Exception e) {
                BetterFishing.LOGGER.error("callback invoke error: ", e);
            }
        }
    }
}
