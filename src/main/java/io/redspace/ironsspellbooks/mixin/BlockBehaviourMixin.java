package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.util.ItemIdContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Blocks must know their registry key when constructed; see {@link ItemIdContext}.
 */
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void irons_spellbooks$applyRegistryId(BlockBehaviour.Properties properties, CallbackInfo ci) {
        var id = ItemIdContext.currentBlock();
        if (id != null) {
            properties.setId(id);
        }
    }
}
