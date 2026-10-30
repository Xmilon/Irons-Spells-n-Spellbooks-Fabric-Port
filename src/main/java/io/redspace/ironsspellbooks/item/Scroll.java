package io.redspace.ironsspellbooks.item;

import net.minecraft.world.InteractionResult;


import io.redspace.ironsspellbooks.api.item.IScroll;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.ScrollSchoolTag;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Scroll extends Item implements IScroll {

    public Scroll() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    private @NotNull SpellData getSpellSlotFromStack(ItemStack itemStack) {
        return ISpellContainer.getOrCreate(itemStack).getSpellAtIndex(0);
    }

    protected void removeScrollAfterCast(ServerPlayer serverPlayer, ItemStack stack) {
        if (!serverPlayer.isCreative()) {
            stack.shrink(1);
        }
    }

    public static void attemptRemoveScrollAfterCast(ServerPlayer serverPlayer) {
        ItemStack potentialScroll = MagicData.getPlayerMagicData(serverPlayer).getPlayerCastingItem();
        if (potentialScroll.getItem() instanceof Scroll scroll) {
            scroll.removeScrollAfterCast(serverPlayer, potentialScroll);
        }
    }


    public @Nullable String getCreatorModId(ItemStack itemStack) {
        var spell = getSpellSlotFromStack(itemStack).getSpell();
        var id = SpellRegistry.REGISTRY.getKey(spell);
        return id == null ? null : id.getNamespace();
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        var spellSlot = getSpellSlotFromStack(stack);
        var spell = spellSlot.getSpell();

        if (level.isClientSide()) {
            if (ClientMagicData.isCasting()) {
                return InteractionResult.CONSUME;
            } else if (!ClientMagicData.getSyncedSpellData(player).isSpellLearned(spell)) {
                return InteractionResult.PASS;
            } else {
                return InteractionResult.CONSUME;
            }
        }

        var castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND : SpellSelectionManager.OFFHAND;

        if (spell.attemptInitiateCast(stack, spell.getLevelFor(spellSlot.getLevel(), player), level, player, CastSource.SCROLL, false, castingSlot)) {
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.FAIL;
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack itemStack) {
        return getSpellSlotFromStack(itemStack).getDisplayName();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull net.minecraft.server.level.ServerLevel level, @NotNull Entity entity, @org.jetbrains.annotations.Nullable net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        ScrollSchoolTag.syncModelData(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, @NotNull java.util.function.Consumer<Component> consumer, @NotNull TooltipFlag flag) {
        var lines = new java.util.ArrayList<Component>();
        appendHoverLines(itemStack, lines);
        lines.forEach(consumer);
    }

    private void appendHoverLines(ItemStack itemStack, List<Component> lines) {
        MinecraftInstanceHelper.ifPlayerPresent(player -> {
            lines.addAll(TooltipsUtils.formatScrollTooltip(itemStack, player));
        });
    }
}


