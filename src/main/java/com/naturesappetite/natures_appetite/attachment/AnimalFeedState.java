package com.naturesappetite.natures_appetite.attachment;

import com.naturesappetite.natures_appetite.datamap.FoodQualityEntry;
import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;

public final class AnimalFeedState {
    private long nextScanTick;
    private UUID blockedItem;
    private long blockedUntilTick;
    private long pathingStartTick;

    private UUID lastAttributedPlayer;
    private long lastAttributedTick;

    private double pendingExtraBabyChance;
    private int pendingBabyGrowthBonusTicks;

    private long signalUntilTick;
    private double signalRangeBoost;

    private long specialDropUntilTick;
    private double specialDropChance;
    private float specialDropMultiplier = 1.0F;

    public long getNextScanTick() {
        return nextScanTick;
    }

    public void setNextScanTick(long nextScanTick) {
        this.nextScanTick = nextScanTick;
    }

    public long getPathingStartTick() {
        return pathingStartTick;
    }

    public void setPathingStartTick(long pathingStartTick) {
        this.pathingStartTick = pathingStartTick;
    }

    public boolean isItemBlocked(ItemEntity itemEntity, long now) {
        return blockedItem != null && blockedUntilTick > now && blockedItem.equals(itemEntity.getUUID());
    }

    public void blockItem(ItemEntity itemEntity, long untilTick) {
        this.blockedItem = itemEntity.getUUID();
        this.blockedUntilTick = untilTick;
    }

    public void clearBlockedItem() {
        this.blockedItem = null;
        this.blockedUntilTick = 0L;
    }

    public void rememberAttribution(UUID playerId, long now) {
        this.lastAttributedPlayer = playerId;
        this.lastAttributedTick = now;
    }

    public UUID getLastAttributedPlayer() {
        return lastAttributedPlayer;
    }

    public long getLastAttributedTick() {
        return lastAttributedTick;
    }

    public void applyQuality(FoodQualityEntry quality, long now) {
        this.pendingExtraBabyChance = Mth.clamp(this.pendingExtraBabyChance + quality.extraBabyChance(), 0.0D, 0.95D);
        this.pendingBabyGrowthBonusTicks = Math.max(this.pendingBabyGrowthBonusTicks, quality.babyGrowthBonusTicks());
        if (quality.signalDurationTicks() > 0 && quality.signalRange() > 0.0D) {
            this.signalUntilTick = Math.max(this.signalUntilTick, now + quality.signalDurationTicks());
            this.signalRangeBoost = Math.max(this.signalRangeBoost, quality.signalRange());
        }
        if (quality.specialDropDurationTicks() > 0) {
            this.specialDropUntilTick = Math.max(this.specialDropUntilTick, now + quality.specialDropDurationTicks());
            this.specialDropChance = Math.max(this.specialDropChance, quality.specialDropChance());
            this.specialDropMultiplier = Math.max(this.specialDropMultiplier, quality.specialDropMultiplier());
        }
    }

    public double consumeExtraBabyChance() {
        double value = this.pendingExtraBabyChance;
        this.pendingExtraBabyChance = 0.0D;
        return value;
    }

    public int consumeBabyGrowthBonusTicks() {
        int value = this.pendingBabyGrowthBonusTicks;
        this.pendingBabyGrowthBonusTicks = 0;
        return value;
    }

    public boolean hasSignal(long now) {
        return now < this.signalUntilTick;
    }

    public double getSignalRangeBoost(long now) {
        return hasSignal(now) ? this.signalRangeBoost : 0.0D;
    }

    public void applySignal(long untilTick, double rangeBoost) {
        this.signalUntilTick = Math.max(this.signalUntilTick, untilTick);
        this.signalRangeBoost = Math.max(this.signalRangeBoost, rangeBoost);
    }

    public boolean hasSpecialDropBuff(long now) {
        return now < this.specialDropUntilTick;
    }

    public double getSpecialDropChance(long now) {
        return hasSpecialDropBuff(now) ? this.specialDropChance : 0.0D;
    }

    public float getSpecialDropMultiplier(long now) {
        return hasSpecialDropBuff(now) ? this.specialDropMultiplier : 1.0F;
    }
}
