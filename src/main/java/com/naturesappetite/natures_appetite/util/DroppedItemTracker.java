package com.naturesappetite.natures_appetite.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

public final class DroppedItemTracker {
    private static final Map<ServerLevel, Set<ItemEntity>> ITEMS_BY_LEVEL = Collections.synchronizedMap(new WeakHashMap<>());

    private DroppedItemTracker() {
    }

    public static void trackJoin(ServerLevel level, ItemEntity itemEntity) {
        synchronized (ITEMS_BY_LEVEL) {
            Set<ItemEntity> tracked = ITEMS_BY_LEVEL.computeIfAbsent(
                    level,
                    key -> Collections.newSetFromMap(new IdentityHashMap<>()));
            tracked.add(itemEntity);
        }
    }

    public static void trackLeave(ServerLevel level, ItemEntity itemEntity) {
        synchronized (ITEMS_BY_LEVEL) {
            Set<ItemEntity> tracked = ITEMS_BY_LEVEL.get(level);
            if (tracked != null) {
                tracked.remove(itemEntity);
                if (tracked.isEmpty()) {
                    ITEMS_BY_LEVEL.remove(level);
                }
            }
        }
    }

    public static void clearLevel(ServerLevel level) {
        synchronized (ITEMS_BY_LEVEL) {
            ITEMS_BY_LEVEL.remove(level);
        }
    }

    public static List<ItemEntity> getCandidates(ServerLevel level, AABB area, int maxCandidates) {
        synchronized (ITEMS_BY_LEVEL) {
            Set<ItemEntity> tracked = ITEMS_BY_LEVEL.get(level);
            if (tracked == null || tracked.isEmpty()) {
                return List.of();
            }

            List<ItemEntity> candidates = new ArrayList<>(Math.min(tracked.size(), maxCandidates));
            Iterator<ItemEntity> iterator = tracked.iterator();
            while (iterator.hasNext()) {
                ItemEntity entity = iterator.next();
                if (entity == null || !entity.isAlive() || entity.level() != level || entity.getItem().isEmpty()) {
                    iterator.remove();
                    continue;
                }

                if (entity.getBoundingBox().intersects(area)) {
                    candidates.add(entity);
                    if (candidates.size() >= maxCandidates) {
                        break;
                    }
                }
            }

            return candidates;
        }
    }
}
