package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// NeoForge's EntityAttributeModificationEvent has no Fabric equivalent; every living entity needs our attributes (mana, spell power, resists)
@Mixin(DefaultAttributes.class)
public class DefaultAttributesMixin {
    private static final Map<AttributeSupplier, AttributeSupplier> irons_spellbooks$cache = new ConcurrentHashMap<>();

    @Inject(method = "getSupplier", at = @At("RETURN"), cancellable = true)
    private static void irons_spellbooks$addModAttributes(EntityType<? extends LivingEntity> type, CallbackInfoReturnable<AttributeSupplier> cir) {
        AttributeSupplier supplier = cir.getReturnValue();
        if (supplier != null) {
            cir.setReturnValue(irons_spellbooks$cache.computeIfAbsent(supplier, AttributeRegistry::withModAttributes));
        }
    }
}
