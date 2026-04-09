package com.naturesappetite.natures_appetite.ai;

public final class FeedEligibilityRules {
    private FeedEligibilityRules() {
    }

    public static boolean canStartFeeding(
            boolean baby,
            int age,
            boolean canFallInLove,
            boolean enableAdultContinuousFeeding,
            boolean enableBabyContinuousFeeding) {
        if (baby) {
            return enableBabyContinuousFeeding;
        }
        if (!enableAdultContinuousFeeding) {
            return false;
        }
        if (age != 0) {
            return false;
        }
        return canFallInLove;
    }
}
