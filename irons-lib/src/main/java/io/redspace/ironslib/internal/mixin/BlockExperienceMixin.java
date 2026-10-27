package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.redspace.ironslib.attribute.AttributeEventsHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public abstract class BlockExperienceMixin {
    @Unique
    private static final ThreadLocal<Entity> irons_lib$breaker = new ThreadLocal<>();

    @WrapMethod(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V")
    private static void irons_lib$rememberBreaker(BlockState state, Level level, BlockPos pos,
                                                  BlockEntity blockEntity, Entity breaker, ItemStack tool,
                                                  Operation<Void> original) {
        Entity previous = irons_lib$breaker.get();
        irons_lib$breaker.set(breaker);
        try {
            original.call(state, level, pos, blockEntity, breaker, tool);
        } finally {
            if (previous == null) {
                irons_lib$breaker.remove();
            } else {
                irons_lib$breaker.set(previous);
            }
        }
    }

    @ModifyVariable(method = "popExperience", at = @At("HEAD"), argsOnly = true)
    private int irons_lib$experience(int experience) {
        return AttributeEventsHandler.modifyExperience(irons_lib$breaker.get(), experience);
    }
}
