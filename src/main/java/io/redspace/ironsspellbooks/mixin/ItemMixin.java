package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.item.UpgradeData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.redspace.ironsspellbooks.util.ItemIdContext;

//Default priority is 1000
@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void irons_spellbooks$applyRegistryId(Item.Properties properties, CallbackInfo ci) {
        var id = ItemIdContext.current();
        if (id != null) {
            properties.setId(id);
        }
    }


    /**
     * Necessary to display how many times a piece of gear has been upgraded on its name
     */
    @Inject(method = "getName", at = @At("TAIL"), cancellable = true)
    public void getHoverName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        if (UpgradeData.hasUpgradeData(stack)) {
            cir.setReturnValue(Component.translatable("tooltip.irons_spellbooks.upgrade_plus_format", cir.getReturnValue(), UpgradeData.getUpgradeData(stack).getTotalUpgrades()));
        }
    }
}
