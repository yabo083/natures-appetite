package com.naturesappetite.natures_appetite.datamap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.naturesappetite.natures_appetite.NaturesAppetiteMod;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModDataMaps {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOOD_QUALITY_PATH = "data_maps/item";
    private static final Map<ResourceLocation, FoodQualityEntry> FOOD_QUALITY = new HashMap<>();

    private ModDataMaps() {
    }

    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new FoodQualityReloadListener());
    }

    public static FoodQualityEntry get(ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) {
            return FoodQualityEntry.NONE;
        }
        synchronized (FOOD_QUALITY) {
            return FOOD_QUALITY.getOrDefault(itemId, FoodQualityEntry.NONE);
        }
    }

    private static final class FoodQualityReloadListener extends SimpleJsonResourceReloadListener {
        private FoodQualityReloadListener() {
            super(GSON, FOOD_QUALITY_PATH);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> loadedFiles, ResourceManager resourceManager, ProfilerFiller profiler) {
            Map<ResourceLocation, FoodQualityEntry> merged = new HashMap<>();
            loadedFiles.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(ResourceLocation::toString)))
                    .forEach(entry -> parseFile(entry.getKey(), entry.getValue(), merged));

            synchronized (FOOD_QUALITY) {
                FOOD_QUALITY.clear();
                FOOD_QUALITY.putAll(merged);
            }
            NaturesAppetiteMod.LOGGER.info("Loaded {} food quality entries", merged.size());
        }

        private static void parseFile(ResourceLocation fileId, JsonElement raw, Map<ResourceLocation, FoodQualityEntry> merged) {
            JsonObject root = GsonHelper.convertToJsonObject(raw, "food_quality");
            if (GsonHelper.getAsBoolean(root, "replace", false)) {
                merged.clear();
            }
            JsonObject values = GsonHelper.getAsJsonObject(root, "values");
            for (Map.Entry<String, JsonElement> valueEntry : values.entrySet()) {
                ResourceLocation itemId = ResourceLocation.tryParse(valueEntry.getKey());
                if (itemId == null) {
                    NaturesAppetiteMod.LOGGER.warn("Skipping invalid item id '{}' in {}", valueEntry.getKey(), fileId);
                    continue;
                }

                DataResult<FoodQualityEntry> parsed = FoodQualityEntry.CODEC.parse(JsonOps.INSTANCE, valueEntry.getValue());
                parsed.resultOrPartial(message -> NaturesAppetiteMod.LOGGER.warn(
                                "Failed to parse food quality entry '{}' from {}: {}",
                                valueEntry.getKey(),
                                fileId,
                                message))
                        .ifPresent(parsedEntry -> merged.put(itemId, parsedEntry));
            }
        }
    }
}
