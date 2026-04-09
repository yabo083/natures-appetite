package com.naturesappetite.natures_appetite.datamap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

class FoodQualityEntryTest {
    @Test
    void codecUsesDefaultValuesWhenFieldsMissing() {
        JsonObject emptyObject = new JsonObject();
        FoodQualityEntry decoded = FoodQualityEntry.CODEC.parse(JsonOps.INSTANCE, emptyObject)
                .getOrThrow(false, message -> {
                });

        assertEquals(FoodQualityEntry.NONE, decoded);
    }

    @Test
    void hasAnyEffectReflectsQualityData() {
        assertFalse(FoodQualityEntry.NONE.hasAnyEffect());
        assertTrue(FoodQualityEntry.GOLDEN_FALLBACK.hasAnyEffect());
    }
}
