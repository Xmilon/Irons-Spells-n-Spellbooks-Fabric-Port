package io.redspace.ironsspellbooks.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.redspace.ironsspellbooks.compat.trinkets.TrinketsApi;
import io.redspace.ironsspellbooks.compat.trinkets.ITrinketItem;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements io.redspace.ironsspellbooks.api.magic.MagicDataHolder {
    @Unique
    private Multimap<Holder<Attribute>, AttributeModifier> irons_spellbooks$curioAttributeCache;
    @Unique
    private io.redspace.ironsspellbooks.api.magic.MagicData irons_spellbooks$magicData;

    @Override
    public io.redspace.ironsspellbooks.api.magic.MagicData irons_spellbooks$getMagicData() {
        return irons_spellbooks$magicData;
    }

    @Override
    public void irons_spellbooks$setMagicData(io.redspace.ironsspellbooks.api.magic.MagicData magicData) {
        this.irons_spellbooks$magicData = magicData;
    }
    @Unique
    private boolean irons_spellbooks$wasFallFlying;

    @Inject(method = "updateEffectVisibility", at = @At(value = "TAIL"))
    public void irons_spellbooks$updateInvisibilityStatus(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY))
            self.setInvisible(true);
    }

    @Inject(method = "isCurrentlyGlowing", at = @At(value = "HEAD"), cancellable = true)
    public void irons_spellbooks$isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide() && self.hasEffect(MobEffectRegistry.GUIDING_BOLT)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    public void irons_spellbooks$changeSummonHurtCredit(net.minecraft.server.level.ServerLevel serverLevel, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        IMagicSummon fromSummon = damageSource.getDirectEntity() instanceof IMagicSummon summon ? summon : damageSource.getEntity() instanceof IMagicSummon summon ? summon : null;
        if (fromSummon instanceof LivingEntity livingSummon) {
            ((LivingEntity) (Object) this).setLastHurtByMob(livingSummon);
        }
    }

    @Shadow
    @org.spongepowered.asm.mixin.Final
    private Map<EquipmentSlot, ItemStack> lastEquipmentItems;

    @Unique
    private static final List<EquipmentSlot> handSlots = List.of(EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND);

    // The equipment change event fires 5 lines too early for this to have been able to be done via events
    @Inject(method = "collectEquipmentChanges", at = @At(value = "RETURN"))
    public void handleEquipmentChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        // Last hand items are accurate at this point
        // Mainhand assigning/removing is handled by minecraft. All we are doing is fudging offhand handling
        // The return of this function is a map of equipmentslots to itemstacks, of itemstacks who have been changed
        var changedEquipment = cir.getReturnValue();
        if (changedEquipment == null) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack toStack = changedEquipment.get(EquipmentSlot.MAINHAND);
        if (toStack == null) {
            // If this stack was not changed, continue
            return;
        }
        ItemStack fromStack = lastEquipmentItems.getOrDefault(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        ItemStack offhandStack = self.getOffhandItem();
        //offhand swap
        if (fromStack == offhandStack) {
            return;
        }
        //Do we even care
        if (!offhandStack.isEmpty() && offhandStack.has(ComponentRegistry.MULTIHAND_WEAPON)) {
            //did we equip a multihand item? (hide offhand)
            if (toStack.has(ComponentRegistry.MULTIHAND_WEAPON)) {
                if (!toStack.isEmpty()) {
                    self.getAttributes().removeAttributeModifiers(filterApplicableAttributes(offhandStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)));
                }
            }
            //did we unequip a multihand item? (reveal offhand)
            else if (fromStack.has(ComponentRegistry.MULTIHAND_WEAPON)) {
                if (!offhandStack.isEmpty()) {
                    self.getAttributes().addTransientAttributeModifiers(filterApplicableAttributes(offhandStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)));
                }
            }
        }
    }

    @Unique
    private static Multimap<Holder<Attribute>, AttributeModifier> filterApplicableAttributes(ItemAttributeModifiers modifiers) {
        var list = modifiers.modifiers().stream().filter(entry -> entry.slot() == EquipmentSlotGroup.MAINHAND).toList();
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        for (ItemAttributeModifiers.Entry entry : list) {
            var predicate = ServerConfigs.safeGet(ServerConfigs.APPLY_ALL_MULTIHAND_ATTRIBUTES) ? Utils.NON_BASE_ATTRIBUTES : Utils.ONLY_MAGIC_ATTRIBUTES;
            if (predicate.test(entry.attribute())) {
                map.put(entry.attribute(), entry.modifier());
            }
        }
        return map;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void irons_spellbooks$applyCurioAttributes(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }

        Multimap<Holder<Attribute>, AttributeModifier> nextModifiers = HashMultimap.create();
        TrinketsApi.getTrinketsInventory(player).ifPresent(curios -> {
            for (var result : curios.findTrinkets(stack -> true)) {
                var stack = result.stack();
                var TrinketSlotContext = result.slotContext();
                if (stack.isEmpty()) {
                    continue;
                }
                if (!(stack.getItem() instanceof ITrinketItem curioItem)) {
                    continue;
                }

                var itemModifiers = curioItem.getAttributeModifiers(TrinketSlotContext, IronsSpellbooks.id("equipped_curio_" + TrinketSlotContext.identifier() + "_" + TrinketSlotContext.index()), stack);
                if (!itemModifiers.isEmpty()) {
                    // Client players can occasionally miss custom attribute instances during init.
                    // Only apply modifiers for attributes that actually exist to avoid render-thread warn spam.
                    itemModifiers.asMap().forEach((attribute, modifiers) -> {
                        if (self.getAttribute(attribute) != null) {
                            for (var modifier : modifiers) {
                                nextModifiers.put(attribute, modifier);
                            }
                        }
                    });
                }
                curioItem.trinketTick(TrinketSlotContext, stack);
            }
        });

        Multimap<Holder<Attribute>, AttributeModifier> previousModifiers = this.irons_spellbooks$curioAttributeCache;
        if (previousModifiers != null && !previousModifiers.isEmpty()) {
            self.getAttributes().removeAttributeModifiers(previousModifiers);
        }
        if (!nextModifiers.isEmpty()) {
            self.getAttributes().addTransientAttributeModifiers(nextModifiers);
        }
        this.irons_spellbooks$curioAttributeCache = ImmutableMultimap.copyOf(nextModifiers);
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"))
    public void irons_spellbooks$captureFallFlyingState(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        irons_spellbooks$wasFallFlying = self.isFallFlying();
    }

    @Inject(method = "updateFallFlying", at = @At("TAIL"))
    public void irons_spellbooks$updateAngelWingsFlight(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        boolean hasAngelWingsEffect = self.hasEffect(MobEffectRegistry.ANGEL_WINGS);
        if (!hasAngelWingsEffect) {
            return;
        }

        // Vanilla handles start/stop conditions. Only keep glide alive if it was already active this tick.
        if (!self.level().isClientSide()
                && self instanceof Player player
                && irons_spellbooks$wasFallFlying
                && !self.onGround()
                && !self.isPassenger()
                && !self.hasEffect(MobEffects.LEVITATION)) {
            ((PlayerAccessor) player).irons_spellbooks$startFallFlying();
        }
    }

}

