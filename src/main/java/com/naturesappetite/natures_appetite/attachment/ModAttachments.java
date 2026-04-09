package com.naturesappetite.natures_appetite.attachment;

import com.naturesappetite.natures_appetite.NaturesAppetiteMod;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            NaturesAppetiteMod.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AnimalFeedState>> ANIMAL_FEED_STATE = ATTACHMENT_TYPES.register(
            "animal_feed_state",
            () -> AttachmentType.builder(AnimalFeedState::new).build());

    private ModAttachments() {
    }

    public static AnimalFeedState get(Animal animal) {
        return animal.getData(ANIMAL_FEED_STATE);
    }
}
