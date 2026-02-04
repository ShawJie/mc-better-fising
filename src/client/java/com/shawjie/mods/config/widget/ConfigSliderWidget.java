package com.shawjie.mods.config.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * A slider widget for integer configuration values
 */
public class ConfigSliderWidget extends AbstractSliderButton {

    private final int minValue;
    private final int maxValue;
    private final Component label;
    private final Consumer<Integer> onValueChange;
    private final String unit;

    /**
     * Creates a new config slider widget
     *
     * @param x X position
     * @param y Y position
     * @param width Widget width
     * @param height Widget height
     * @param label Label to display before the value
     * @param minValue Minimum value (inclusive)
     * @param maxValue Maximum value (inclusive)
     * @param initialValue Initial value
     * @param unit Unit to display after the value (e.g., "ticks")
     * @param onValueChange Callback when value changes
     */
    public ConfigSliderWidget(int x, int y, int width, int height,
                              Component label, int minValue, int maxValue,
                              int initialValue, String unit,
                              Consumer<Integer> onValueChange) {
        super(x, y, width, height, Component.empty(),
              (double)(initialValue - minValue) / (maxValue - minValue));
        this.label = label;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unit = unit;
        this.onValueChange = onValueChange;
        updateMessage();
    }

    /**
     * Gets the current integer value
     */
    public int getIntValue() {
        return (int) Math.round(minValue + value * (maxValue - minValue));
    }

    /**
     * Sets the integer value
     */
    public void setIntValue(int newValue) {
        int clampedValue = Math.max(minValue, Math.min(maxValue, newValue));
        this.value = (double)(clampedValue - minValue) / (maxValue - minValue);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        String valueText = String.valueOf(getIntValue());
        if (unit != null && !unit.isEmpty()) {
            valueText += " " + unit;
        }
        setMessage(Component.literal(label.getString() + ": " + valueText));
    }

    @Override
    protected void applyValue() {
        if (onValueChange != null) {
            onValueChange.accept(getIntValue());
        }
    }

    /**
     * Gets the minimum value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Gets the maximum value
     */
    public int getMaxValue() {
        return maxValue;
    }
}
