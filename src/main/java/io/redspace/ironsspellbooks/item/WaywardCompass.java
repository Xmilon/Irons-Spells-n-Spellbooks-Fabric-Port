package io.redspace.ironsspellbooks.item;

import net.minecraft.world.InteractionResult;

import io.redspace.ironsspellbooks.api.item.WaywardCompassData;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WaywardCompass extends Item {
    private static final Component description = Component.translatable("item.irons_spellbooks.wayward_compass_desc").withStyle(ChatFormatting.DARK_AQUA);

    public WaywardCompass() {
        super(ItemPropertiesHelper.equipment());
    }

    @Nullable
    public static GlobalPos getCatacombsLocation(Entity entity, ItemStack stack) {
        if (!(entity.level().dimension() == Level.OVERWORLD && stack.has(ComponentRegistry.WAYWARD_COMPASS)))
            return null;

        return GlobalPos.of(entity.level().dimension(), stack.get(ComponentRegistry.WAYWARD_COMPASS).blockPos());
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Player pPlayer) {
        findCatacombs(pStack, pPlayer.level(), pPlayer);
    }

    private static void findCatacombs(ItemStack pStack, Level pLevel, Player pPlayer) {
        if (pLevel instanceof ServerLevel serverlevel) {
            BlockPos blockpos = serverlevel.findNearestMapStructure(ModTags.WAYWARD_COMPASS_LOCATOR, pPlayer.blockPosition(), 100, false);
            if (blockpos != null) {
                pStack.set(ComponentRegistry.WAYWARD_COMPASS, new WaywardCompassData(blockpos));
                syncLodestoneTarget(pStack);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        syncLodestoneTarget(stack);
    }

    private static void syncLodestoneTarget(ItemStack stack) {
        var data = stack.get(ComponentRegistry.WAYWARD_COMPASS);
        if (data == null) {
            stack.remove(DataComponents.LODESTONE_TRACKER);
            return;
        }
        var target = data.lodestoneTracker();
        if (!target.equals(stack.get(DataComponents.LODESTONE_TRACKER))) {
            stack.set(DataComponents.LODESTONE_TRACKER, target);
        }
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (missingWarning(itemStack)) {
            findCatacombs(itemStack, pLevel, pPlayer);
            pPlayer.getCooldowns().addCooldown(itemStack, 200);
            return InteractionResult.SUCCESS;
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    public boolean missingWarning(ItemStack itemStack) {
        return !itemStack.has(ComponentRegistry.WAYWARD_COMPASS);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag pIsAdvanced) {
        tooltipAdder.accept(description);
        if (missingWarning(pStack)) {
            tooltipAdder.accept(Component.translatable("item.irons_spellbooks.wayward_compass.error", Component.keybind("key.use")).withStyle(ChatFormatting.RED));
        }
    }
}
