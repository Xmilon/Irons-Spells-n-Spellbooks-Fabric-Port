package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Marks entities whose Oakskin came from an elixir. Not persisted across logins.
 */
public final class OakskinData {
    private static final Set<LivingEntity> FROM_ELIXIR = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private OakskinData() {
    }

    public static void setFromElixir(LivingEntity entity) {
        FROM_ELIXIR.add(entity);
    }

    public static void remove(LivingEntity livingEntity) {
        FROM_ELIXIR.remove(livingEntity);
    }

    public static boolean hasFromElixir(LivingEntity livingEntity) {
        return FROM_ELIXIR.contains(livingEntity);
    }
}
