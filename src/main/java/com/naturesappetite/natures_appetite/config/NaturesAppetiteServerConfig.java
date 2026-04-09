package com.naturesappetite.natures_appetite.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class NaturesAppetiteServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_AUTO_FEED = BUILDER
            .comment("Master switch for automatic feeding and breeding.")
            .define("enableAutoFeed", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_ADULT_CONTINUOUS_FEEDING = BUILDER
            .comment("Allow adult animals to auto-feed. Adults still require no breeding cooldown and no active in-love timer.")
            .define("enableAdultContinuousFeeding", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_BABY_CONTINUOUS_FEEDING = BUILDER
            .comment("Allow baby animals to continuously consume dropped food for growth.")
            .define("enableBabyContinuousFeeding", true);

    private static final ForgeConfigSpec.DoubleValue SCAN_RADIUS = BUILDER
            .comment("Horizontal/vertical radius for scanning dropped food.")
            .defineInRange("scanRadius", 8.0D, 2.0D, 64.0D);

    private static final ForgeConfigSpec.IntValue SCAN_INTERVAL_MIN = BUILDER
            .comment("Minimum scan interval in ticks.")
            .defineInRange("scanIntervalMin", 20, 5, 1200);

    private static final ForgeConfigSpec.IntValue SCAN_INTERVAL_MAX = BUILDER
            .comment("Maximum scan interval in ticks.")
            .defineInRange("scanIntervalMax", 40, 5, 1200);

    private static final ForgeConfigSpec.IntValue MAX_CANDIDATES_PER_SCAN = BUILDER
            .comment("Maximum number of tracked dropped items to evaluate per scan.")
            .defineInRange("maxCandidatesPerScan", 24, 1, 256);

    private static final ForgeConfigSpec.IntValue PATH_TIMEOUT_TICKS = BUILDER
            .comment("Path timeout before a target item gets temporary backoff.")
            .defineInRange("pathTimeoutTicks", 120, 20, 2400);

    private static final ForgeConfigSpec.DoubleValue OWNER_ATTRIBUTION_RANGE = BUILDER
            .comment("Fallback range for attributing auto-feed to a nearby player.")
            .defineInRange("ownerAttributionRange", 8.0D, 1.0D, 64.0D);

    private static final ForgeConfigSpec.BooleanValue ENABLE_QUALITY_SYSTEM = BUILDER
            .comment("Enable quality data map effects.")
            .define("enableQualitySystem", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_PACK_BEHAVIOR = BUILDER
            .comment("Enable feeding signals to nearby same-species animals.")
            .define("enablePackBehavior", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_SPECIAL_DROPS = BUILDER
            .comment("Enable quality-triggered special drop buffs.")
            .define("enableSpecialDrops", true);

    private static final ForgeConfigSpec.IntValue GOAL_PRIORITY = BUILDER
            .comment("Inserted goal priority. Lower values run earlier.")
            .defineInRange("goalPriority", 4, 0, 20);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private NaturesAppetiteServerConfig() {
    }

    public static boolean enableAutoFeed() {
        return ENABLE_AUTO_FEED.get();
    }

    public static boolean enableAdultContinuousFeeding() {
        return ENABLE_ADULT_CONTINUOUS_FEEDING.get();
    }

    public static boolean enableBabyContinuousFeeding() {
        return ENABLE_BABY_CONTINUOUS_FEEDING.get();
    }

    public static double scanRadius() {
        return SCAN_RADIUS.get();
    }

    public static int scanIntervalMin() {
        return SCAN_INTERVAL_MIN.get();
    }

    public static int scanIntervalMax() {
        return Math.max(scanIntervalMin(), SCAN_INTERVAL_MAX.get());
    }

    public static int maxCandidatesPerScan() {
        return MAX_CANDIDATES_PER_SCAN.get();
    }

    public static int pathTimeoutTicks() {
        return PATH_TIMEOUT_TICKS.get();
    }

    public static double ownerAttributionRange() {
        return OWNER_ATTRIBUTION_RANGE.get();
    }

    public static boolean enableQualitySystem() {
        return ENABLE_QUALITY_SYSTEM.get();
    }

    public static boolean enablePackBehavior() {
        return ENABLE_PACK_BEHAVIOR.get();
    }

    public static boolean enableSpecialDrops() {
        return ENABLE_SPECIAL_DROPS.get();
    }

    public static int goalPriority() {
        return GOAL_PRIORITY.get();
    }
}
