package io.redspace.ironsspellbooks.api.magic;

/**
 * Implemented on LivingEntity by a mixin. MagicData lives on the entity object itself: a map keyed by entity would collide
 * the client and server player in singleplayer, since Entity#equals/hashCode only compare entity ids.
 */
public interface MagicDataHolder {
    MagicData irons_spellbooks$getMagicData();

    void irons_spellbooks$setMagicData(MagicData magicData);
}
