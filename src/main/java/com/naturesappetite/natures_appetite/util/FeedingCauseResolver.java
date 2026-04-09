package com.naturesappetite.natures_appetite.util;

import com.naturesappetite.natures_appetite.attachment.AnimalFeedState;
import com.naturesappetite.natures_appetite.attachment.ModAttachments;
import com.naturesappetite.natures_appetite.config.NaturesAppetiteServerConfig;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class FeedingCauseResolver {
    private static final long ATTRIBUTION_MEMORY_TICKS = 20L * 30L;

    private FeedingCauseResolver() {
    }

    @Nullable
    public static ServerPlayer resolveAndRemember(Animal animal, ItemEntity consumedItem) {
        if (!(animal.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        long now = serverLevel.getGameTime();
        AnimalFeedState state = ModAttachments.get(animal);

        Entity owner = consumedItem.getOwner();
        if (owner instanceof ServerPlayer ownerPlayer) {
            state.rememberAttribution(ownerPlayer.getUUID(), now);
            return ownerPlayer;
        }

        Player nearestPlayer = serverLevel.getNearestPlayer(
                animal,
                NaturesAppetiteServerConfig.ownerAttributionRange());
        if (nearestPlayer instanceof ServerPlayer nearbyPlayer) {
            state.rememberAttribution(nearbyPlayer.getUUID(), now);
            return nearbyPlayer;
        }

        UUID rememberedId = state.getLastAttributedPlayer();
        if (rememberedId != null && now - state.getLastAttributedTick() <= ATTRIBUTION_MEMORY_TICKS) {
            return serverLevel.getServer().getPlayerList().getPlayer(rememberedId);
        }

        return null;
    }
}
