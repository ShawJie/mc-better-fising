package com.shawjie.mods.property;

import java.util.Set;

public class BetterFishingConfigurationProperties {

    private Boolean autoFishingEnable;

    private Integer pullUpTick;
    private Integer releaseTick;

    private Set<String> blockListItems;
    private Boolean stopBeforeRodBreak;

    public Boolean getAutoFishingEnable() {
        return autoFishingEnable;
    }

    public void setAutoFishingEnable(Boolean autoFishingEnable) {
        this.autoFishingEnable = autoFishingEnable;
    }

    public Integer getPullUpTick() {
        return pullUpTick;
    }

    public void setPullUpTick(Integer pullUpTick) {
        this.pullUpTick = pullUpTick;
    }

    public Integer getReleaseTick() {
        return releaseTick;
    }

    public void setReleaseTick(Integer releaseTick) {
        this.releaseTick = releaseTick;
    }

    public Set<String> getBlockListItems() {
        return blockListItems;
    }

    public void setBlockListItems(Set<String> blockListItems) {
        this.blockListItems = blockListItems;
    }

    public Boolean getStopBeforeRodBreak() {
        return stopBeforeRodBreak;
    }

    public void setStopBeforeRodBreak(Boolean stopBeforeRodBreak) {
        this.stopBeforeRodBreak = stopBeforeRodBreak;
    }
}
