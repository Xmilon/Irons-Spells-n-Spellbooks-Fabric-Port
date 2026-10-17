package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Per-entity echoing strike charges. Not persisted: the hits only last as long as the effect.
 */
public class EchoingStrikesData {
    private static final Map<LivingEntity, EchoingStrikesData> DATA = Collections.synchronizedMap(new WeakHashMap<>());

    private int hitCount;
    public int vfxTimestamp;

    public EchoingStrikesData(int hitsRemaining) {
        this.hitCount = hitsRemaining;
    }

    public EchoingStrikesData() {
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public boolean hasHitsRemaining() {
        return hitCount > 0;
    }

    public void decrementHit() {
        if (hitCount > 0) {
            hitCount--;
        }
    }

    public void clear() {
        hitCount = 0;
    }

    public static EchoingStrikesData get(LivingEntity entity) {
        return DATA.computeIfAbsent(entity, e -> new EchoingStrikesData());
    }

    public static void remove(LivingEntity livingEntity) {
        DATA.remove(livingEntity);
    }

    public static boolean has(LivingEntity livingEntity) {
        var data = DATA.get(livingEntity);
        return data != null && data.hasHitsRemaining();
    }
}
