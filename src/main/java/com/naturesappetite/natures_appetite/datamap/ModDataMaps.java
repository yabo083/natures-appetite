package com.naturesappetite.natures_appetite.datamap;

import com.naturesappetite.natures_appetite.NaturesAppetiteMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = NaturesAppetiteMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataMaps {
    public static final DataMapType<Item, FoodQualityEntry> FOOD_QUALITY = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(NaturesAppetiteMod.MODID, "food_quality"),
            Registries.ITEM,
            FoodQualityEntry.CODEC)
            .synced(FoodQualityEntry.CODEC, false)
            .build();

    private ModDataMaps() {
    }

    @SubscribeEvent
    public static void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(FOOD_QUALITY);
    }
}
