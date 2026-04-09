package com.naturesappetite.natures_appetite.util;

import com.naturesappetite.natures_appetite.datamap.FoodQualityEntry;
import com.naturesappetite.natures_appetite.datamap.ModDataMaps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class FoodQualityResolver {
    private FoodQualityResolver() {
    }

    public static FoodQualityEntry resolve(ItemStack stack) {
        FoodQualityEntry mapped = ModDataMaps.get(stack);
        if (mapped.hasAnyEffect()) {
            return mapped;
        }
        if (stack.is(Items.GOLDEN_CARROT) || stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return FoodQualityEntry.GOLDEN_FALLBACK;
        }
        return FoodQualityEntry.NONE;
    }
}
