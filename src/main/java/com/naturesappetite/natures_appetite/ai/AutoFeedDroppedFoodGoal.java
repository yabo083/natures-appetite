package com.naturesappetite.natures_appetite.ai;

import com.naturesappetite.natures_appetite.attachment.AnimalFeedState;
import com.naturesappetite.natures_appetite.attachment.ModAttachments;
import com.naturesappetite.natures_appetite.config.NaturesAppetiteServerConfig;
import com.naturesappetite.natures_appetite.datamap.FoodQualityEntry;
import com.naturesappetite.natures_appetite.event.ModGameplayEvents;
import com.naturesappetite.natures_appetite.util.DroppedItemTracker;
import com.naturesappetite.natures_appetite.util.FeedingCauseResolver;
import com.naturesappetite.natures_appetite.util.FoodQualityResolver;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public final class AutoFeedDroppedFoodGoal extends Goal {
    private static final double FEED_DISTANCE_SQR = 2.25D;
    private static final int PATH_RETRY_INTERVAL = 10;
    private static final int SIGNAL_MIN_SCAN_INTERVAL = 5;

    private final Animal animal;
    private final double moveSpeed;
    private ItemEntity targetItem;
    private long nextPathRecalcTick;

    public AutoFeedDroppedFoodGoal(Animal animal, double moveSpeed) {
        this.animal = animal;
        this.moveSpeed = moveSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!NaturesAppetiteServerConfig.enableAutoFeed() || !(animal.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (!animal.isAlive() || !canStartFeedingNow() || !ModGameplayEvents.isSupportedAnimal(animal)) {
            return false;
        }

        AnimalFeedState state = ModAttachments.get(animal);
        long now = serverLevel.getGameTime();
        if (now < state.getNextScanTick()) {
            return false;
        }

        int nextInterval = randomScanInterval(serverLevel, state.hasSignal(now));
        state.setNextScanTick(now + nextInterval);

        double scanRadius = NaturesAppetiteServerConfig.scanRadius() + state.getSignalRangeBoost(now);
        AABB area = animal.getBoundingBox().inflate(scanRadius);
        List<ItemEntity> candidates = DroppedItemTracker.getCandidates(
                serverLevel,
                area,
                NaturesAppetiteServerConfig.maxCandidatesPerScan());
        ItemEntity best = pickNearestValidCandidate(candidates, state, now);
        if (best == null) {
            return false;
        }

        this.targetItem = best;
        state.setPathingStartTick(now);
        this.nextPathRecalcTick = 0L;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return targetItem != null
                && targetItem.isAlive()
                && !targetItem.getItem().isEmpty()
                && canStartFeedingNow()
                && animal.isAlive();
    }

    @Override
    public void start() {
        this.nextPathRecalcTick = 0L;
    }

    @Override
    public void tick() {
        if (targetItem == null || !(animal.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AnimalFeedState state = ModAttachments.get(animal);
        long now = serverLevel.getGameTime();

        if (animal.distanceToSqr(targetItem) <= FEED_DISTANCE_SQR) {
            consumeTarget(serverLevel, state, targetItem);
            stop();
            return;
        }

        animal.getLookControl().setLookAt(targetItem, 30.0F, animal.getMaxHeadXRot());
        if (now >= nextPathRecalcTick) {
            animal.getNavigation().moveTo(targetItem, moveSpeed);
            nextPathRecalcTick = now + PATH_RETRY_INTERVAL;
        }

        if (now - state.getPathingStartTick() >= NaturesAppetiteServerConfig.pathTimeoutTicks()) {
            state.blockItem(targetItem, now + NaturesAppetiteServerConfig.pathTimeoutTicks());
            stop();
        }
    }

    @Override
    public void stop() {
        animal.getNavigation().stop();
        targetItem = null;
    }

    private ItemEntity pickNearestValidCandidate(List<ItemEntity> candidates, AnimalFeedState state, long now) {
        ItemEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (ItemEntity item : candidates) {
            if (item == null || !item.isAlive() || item.getItem().isEmpty() || state.isItemBlocked(item, now)) {
                continue;
            }

            ItemStack stack = item.getItem();
            if (!animal.isFood(stack)) {
                continue;
            }

            double distance = animal.distanceToSqr(item);
            if (distance < bestDistance) {
                best = item;
                bestDistance = distance;
            }
        }
        return best;
    }

    private void consumeTarget(ServerLevel level, AnimalFeedState state, ItemEntity consumedItem) {
        ItemStack stack = consumedItem.getItem();
        if (stack.isEmpty()) {
            return;
        }

        ItemStack consumed = stack.copy();
        consumed.setCount(1);
        stack.shrink(1);
        if (stack.isEmpty()) {
            consumedItem.discard();
        } else {
            consumedItem.setItem(stack);
        }

        ServerPlayer causePlayer = FeedingCauseResolver.resolveAndRemember(animal, consumedItem);
        boolean baby = animal.isBaby();
        if (baby) {
            animal.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-animal.getAge()), true);
        } else {
            animal.setInLove(causePlayer);
        }

        if (baby || !NaturesAppetiteServerConfig.enableQualitySystem()) {
            return;
        }

        FoodQualityEntry quality = FoodQualityResolver.resolve(consumed);
        if (!quality.hasAnyEffect()) {
            return;
        }

        if (quality.healAmount() > 0.0F) {
            animal.heal(quality.healAmount());
        }
        if (quality.loveTimeBonusTicks() > 0) {
            animal.setInLoveTime(animal.getInLoveTime() + quality.loveTimeBonusTicks());
        }

        long now = level.getGameTime();
        state.applyQuality(quality, now);
        if (NaturesAppetiteServerConfig.enablePackBehavior() && quality.signalDurationTicks() > 0 && quality.signalRange() > 0.0D) {
            ModGameplayEvents.broadcastFeedSignal(animal, quality, now);
        }
    }

    private boolean canStartFeedingNow() {
        return FeedEligibilityRules.canStartFeeding(
                animal.isBaby(),
                animal.getAge(),
                animal.canFallInLove(),
                NaturesAppetiteServerConfig.enableAdultContinuousFeeding(),
                NaturesAppetiteServerConfig.enableBabyContinuousFeeding());
    }

    private int randomScanInterval(ServerLevel level, boolean signaled) {
        int min = NaturesAppetiteServerConfig.scanIntervalMin();
        int max = NaturesAppetiteServerConfig.scanIntervalMax();
        int interval = min >= max ? min : Mth.nextInt(level.random, min, max);
        if (signaled) {
            interval = Math.max(SIGNAL_MIN_SCAN_INTERVAL, interval / 2);
        }
        return interval;
    }
}
