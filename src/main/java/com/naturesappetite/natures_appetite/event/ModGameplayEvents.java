package com.naturesappetite.natures_appetite.event;

import com.naturesappetite.natures_appetite.NaturesAppetiteMod;
import com.naturesappetite.natures_appetite.ai.AutoFeedDroppedFoodGoal;
import com.naturesappetite.natures_appetite.attachment.AnimalFeedState;
import com.naturesappetite.natures_appetite.attachment.ModAttachments;
import com.naturesappetite.natures_appetite.config.NaturesAppetiteServerConfig;
import com.naturesappetite.natures_appetite.datamap.FoodQualityEntry;
import com.naturesappetite.natures_appetite.gametest.AutoFeedGameTests;
import com.naturesappetite.natures_appetite.tag.ModTags;
import com.naturesappetite.natures_appetite.util.DroppedItemTracker;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NaturesAppetiteMod.MODID)
public final class ModGameplayEvents {
    private static final int EXTRA_BABY_GROWTH_FALLBACK = -24000;

    private ModGameplayEvents() {
    }

    public static boolean isSupportedAnimal(Animal animal) {
        EntityType<?> entityType = animal.getType();
        if (entityType.is(ModTags.AUTO_FEED_BLACKLIST)) {
            return false;
        }
        return entityType.is(ModTags.AUTO_FEED_ANIMALS);
    }

    public static void broadcastFeedSignal(Animal source, FoodQualityEntry quality, long now) {
        if (!(source.level() instanceof ServerLevel)) {
            return;
        }

        AABB area = source.getBoundingBox().inflate(quality.signalRange());
        Class<? extends Animal> animalClass = source.getClass();
        List<? extends Animal> nearby = source.level().getEntitiesOfClass(
                animalClass,
                area,
                animal -> animal != source && animal.isAlive() && isSupportedAnimal(animal));
        long signalUntil = now + quality.signalDurationTicks();
        for (Animal neighbor : nearby) {
            ModAttachments.get(neighbor).applySignal(signalUntil, quality.signalRange());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (event.getEntity() instanceof ItemEntity itemEntity) {
            DroppedItemTracker.trackJoin(serverLevel, itemEntity);
            return;
        }

        if (!NaturesAppetiteServerConfig.enableAutoFeed() || !(event.getEntity() instanceof Animal animal) || !isSupportedAnimal(animal)) {
            return;
        }

        for (WrappedGoal wrappedGoal : animal.goalSelector.getAvailableGoals()) {
            Goal goal = wrappedGoal.getGoal();
            if (goal instanceof AutoFeedDroppedFoodGoal) {
                return;
            }
        }

        animal.goalSelector.addGoal(
                NaturesAppetiteServerConfig.goalPriority(),
                new AutoFeedDroppedFoodGoal(animal, 1.1D));
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            DroppedItemTracker.trackLeave(level, itemEntity);
        }
        if (event.getEntity() instanceof Animal animal) {
            ModAttachments.remove(animal);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            DroppedItemTracker.clearLevel(serverLevel);
            ModAttachments.clearLevel(serverLevel);
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!NaturesAppetiteServerConfig.enableQualitySystem()
                || !(event.getParentA() instanceof Animal parentA)
                || !(event.getParentB() instanceof Animal parentB)
                || !(parentA.level() instanceof ServerLevel level)) {
            return;
        }

        AnimalFeedState stateA = ModAttachments.get(parentA);
        AnimalFeedState stateB = ModAttachments.get(parentB);

        AgeableMob child = event.getChild();
        int growthBonus = Math.max(stateA.consumeBabyGrowthBonusTicks(), stateB.consumeBabyGrowthBonusTicks());
        if (child != null && growthBonus > 0) {
            child.setAge(Math.min(0, child.getAge() + growthBonus));
        }

        double extraBabyChance = Mth.clamp(stateA.consumeExtraBabyChance() + stateB.consumeExtraBabyChance(), 0.0D, 0.95D);
        if (extraBabyChance <= 0.0D || level.random.nextDouble() > extraBabyChance) {
            return;
        }

        AgeableMob extraChild = parentA.getBreedOffspring(level, parentB);
        if (extraChild == null) {
            return;
        }

        if (child != null) {
            extraChild.moveTo(child.getX(), child.getY(), child.getZ(), child.getYRot(), child.getXRot());
        } else {
            extraChild.moveTo(parentA.getX(), parentA.getY(), parentA.getZ(), parentA.getYRot(), parentA.getXRot());
        }
        if (extraChild.getAge() >= 0) {
            extraChild.setAge(EXTRA_BABY_GROWTH_FALLBACK);
        }
        if (growthBonus > 0) {
            extraChild.setAge(Math.min(0, extraChild.getAge() + growthBonus));
        }
        level.addFreshEntity(extraChild);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!NaturesAppetiteServerConfig.enableSpecialDrops()
                || !(event.getEntity() instanceof Animal animal)
                || !(animal.level() instanceof ServerLevel level)) {
            return;
        }

        AnimalFeedState state = ModAttachments.get(animal);
        long now = level.getGameTime();
        if (!state.hasSpecialDropBuff(now)) {
            return;
        }

        double dropChance = state.getSpecialDropChance(now);
        float multiplier = state.getSpecialDropMultiplier(now);
        if (dropChance <= 0.0D || multiplier <= 1.0F) {
            return;
        }

        List<ItemEntity> extraDrops = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            if (drop.getItem().isEmpty() || level.random.nextDouble() > dropChance) {
                continue;
            }

            int baseCount = drop.getItem().getCount();
            int bonusCount = Math.max(1, Math.round(baseCount * (multiplier - 1.0F)));
            var bonusStack = drop.getItem().copy();
            bonusStack.setCount(bonusCount);
            ItemEntity extraDrop = new ItemEntity(
                    level,
                    drop.getX(),
                    drop.getY(),
                    drop.getZ(),
                    bonusStack);
            extraDrops.add(extraDrop);
        }
        event.getDrops().addAll(extraDrops);
    }

    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        event.register(AutoFeedGameTests.class);
    }
}
