package com.naturesappetite.natures_appetite.util;

import com.naturesappetite.natures_appetite.datamap.FoodQualityEntry;
import com.naturesappetite.natures_appetite.datamap.ModDataMaps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

public final class FoodQualityResolver {
    private FoodQualityResolver() {
    }

    public static FoodQualityEntry resolve(ItemStack stack) {
        FoodQualityEntry mapped = stack.getItemHolder().getData(ModDataMaps.FOOD_QUALITY);
        if (mapped != null) {
            return mapped;
        }
        if (stack.is(Tags.Items.FOODS_GOLDEN)) {
            return FoodQualityEntry.GOLDEN_FALLBACK;
        }
        return FoodQualityEntry.NONE;
    }
}
