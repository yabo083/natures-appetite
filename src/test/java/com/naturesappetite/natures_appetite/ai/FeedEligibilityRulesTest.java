package com.naturesappetite.natures_appetite.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FeedEligibilityRulesTest {
    @Test
    void adultNeedsNoBreedingCooldownWhenContinuousFeedingEnabled() {
        assertFalse(FeedEligibilityRules.canStartFeeding(false, 200, true, true, true));
        assertTrue(FeedEligibilityRules.canStartFeeding(false, 0, true, true, true));
    }

    @Test
    void adultStopsWhenAlreadyInLoveEvenWithoutCooldown() {
        assertFalse(FeedEligibilityRules.canStartFeeding(false, 0, false, true, true));
    }

    @Test
    void adultCanBeFullyDisabled() {
        assertFalse(FeedEligibilityRules.canStartFeeding(false, 0, true, false, true));
    }

    @Test
    void babyContinuousFeedingDefaultsToSwitchValue() {
        assertTrue(FeedEligibilityRules.canStartFeeding(true, -1000, true, true, true));
        assertFalse(FeedEligibilityRules.canStartFeeding(true, -1000, true, true, false));
    }
}
