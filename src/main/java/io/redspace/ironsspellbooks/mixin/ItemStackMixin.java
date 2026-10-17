package io.redspace.ironsspellbooks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "Lnet/minecraft/world/item/ItemStack;<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("TAIL"))
    public void init(ItemLike itemLike, int pCount, PatchedDataComponentMap pComponents, CallbackInfo ci) {
        if (itemLike instanceof IPresetSpellContainer iPresetSpellContainer) {
            iPresetSpellContainer.initializeSpellContainer((ItemStack) (Object) this);
        }
    }

    // Fabric has no ItemAttributeModifierEvent in 1.21.1; post it ourselves so upgrade orbs apply their attributes
    @ModifyExpressionValue(method = {
            "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V",
            "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object irons_spellbooks$postAttributeModifierEvent(Object original) {
        var stack = (ItemStack) (Object) this;
        if (!(original instanceof ItemAttributeModifiers modifiers) || UpgradeData.getUpgradeData(stack) == UpgradeData.NONE) {
            return original;
        }
        var entries = new java.util.ArrayList<>(modifiers.modifiers());
        NeoForge.EVENT_BUS.post(new ItemAttributeModifierEvent(stack, entries));
        return new ItemAttributeModifiers(java.util.List.copyOf(entries), modifiers.showInTooltip());
    }
}
