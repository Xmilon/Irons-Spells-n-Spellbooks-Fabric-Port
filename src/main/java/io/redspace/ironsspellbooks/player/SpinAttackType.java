package io.redspace.ironsspellbooks.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;

public record SpinAttackType(Identifier textureId, boolean fullbright) {
    public static final SpinAttackType FIRE = new SpinAttackType(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_riptide.png"), true);
    public static final SpinAttackType LIGHTNING = new SpinAttackType(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/lightning_riptide.png"), true);
    public static final SpinAttackType RIPTIDE = new SpinAttackType(Identifier.withDefaultNamespace("textures/entity/trident_riptide.png"), false);
}
