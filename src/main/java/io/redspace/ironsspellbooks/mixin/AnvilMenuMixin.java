package io.redspace.ironsspellbooks.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts AnvilUpdateEvent so recipes like the shriving stone can supply a custom anvil result. */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    private int repairItemCountCost;
    @Shadow
    private String itemName;
    @Shadow
    @Final
    private DataSlot cost;

    private AnvilMenuMixin(MenuType<?> type, int containerId, Inventory inventory, ContainerLevelAccess access, net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition slots) {
        super(type, containerId, inventory, access, slots);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$anvilUpdateEvent(CallbackInfo ci) {
        ItemStack left = this.inputSlots.getItem(0);
        ItemStack right = this.inputSlots.getItem(1);
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }
        var event = NeoForge.EVENT_BUS.post(new AnvilUpdateEvent(left, right, this.itemName));
        if (!event.getOutput().isEmpty()) {
            this.resultSlots.setItem(0, event.getOutput());
            this.cost.set(event.getCost());
            this.repairItemCountCost = event.getMaterialCost();
            ci.cancel();
        }
    }
}
