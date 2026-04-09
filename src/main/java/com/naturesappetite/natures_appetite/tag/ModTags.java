package com.naturesappetite.natures_appetite.tag;

import com.naturesappetite.natures_appetite.NaturesAppetiteMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModTags {
    public static final TagKey<EntityType<?>> AUTO_FEED_ANIMALS = TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(NaturesAppetiteMod.MODID, "auto_feed_animals"));

    public static final TagKey<EntityType<?>> AUTO_FEED_BLACKLIST = TagKey.create(
            Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(NaturesAppetiteMod.MODID, "auto_feed_blacklist"));

    private ModTags() {
    }
}
