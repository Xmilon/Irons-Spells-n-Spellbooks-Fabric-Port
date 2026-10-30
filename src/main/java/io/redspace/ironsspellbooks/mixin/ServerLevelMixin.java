package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.util.NoopWorldBorder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The pocket dimension has no world border. */
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Unique
    private WorldBorder irons_spellbooks$noopBorder;

    @Inject(method = "getWorldBorder", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$noopWorldBorder(CallbackInfoReturnable<WorldBorder> cir) {
        if (((ServerLevel) (Object) this).dimension().equals(PocketDimensionManager.POCKET_DIMENSION)) {
            if (irons_spellbooks$noopBorder == null) {
                irons_spellbooks$noopBorder = new NoopWorldBorder();
            }
            cir.setReturnValue(irons_spellbooks$noopBorder);
        }
    }
}
