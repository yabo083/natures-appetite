package com.naturesappetite.natures_appetite.attachment;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;

public final class ModAttachments {
    private static final Map<Animal, AnimalFeedState> STATES = Collections.synchronizedMap(new WeakHashMap<>());

    private ModAttachments() {
    }

    public static AnimalFeedState get(Animal animal) {
        synchronized (STATES) {
            return STATES.computeIfAbsent(animal, key -> new AnimalFeedState());
        }
    }

    public static void remove(Animal animal) {
        synchronized (STATES) {
            STATES.remove(animal);
        }
    }

    public static void clearLevel(ServerLevel level) {
        synchronized (STATES) {
            STATES.keySet().removeIf(animal -> animal == null || !animal.isAlive() || animal.level() == level);
        }
    }
}
