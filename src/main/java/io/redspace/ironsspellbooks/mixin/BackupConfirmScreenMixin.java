package io.redspace.ironsspellbooks.mixin;

import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Skips the "Experimental Settings" prompt shown when opening a world
@Mixin(BackupConfirmScreen.class)
public abstract class BackupConfirmScreenMixin {
    @Shadow
    @Final
    protected BackupConfirmScreen.Listener onProceed;

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$skipExperimentalWarning(CallbackInfo ci) {
        var self = (BackupConfirmScreen) (Object) this;
        if (self.getTitle().getContents() instanceof TranslatableContents t && "selectWorld.backupQuestion.experimental".equals(t.getKey())) {
            onProceed.proceed(false, false);
            ci.cancel();
        }
    }
}
