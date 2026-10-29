package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;


public class HeartstopEffect extends MagicMobEffect implements ISyncedMobEffect {

    public HeartstopEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        if (!(pLivingEntity.level() instanceof ServerLevel level)) return;
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        var playerMagicData = MagicData.getPlayerMagicData(pLivingEntity);
        //Whether or not player has spawn immunity (we want to damage them regardless)
        if (pLivingEntity.tickCount > 60) {
            pLivingEntity.hurtServer(level, DamageSources.get(level, ISSDamageTypes.HEARTSTOP), playerMagicData.getSyncedData().getHeartstopAccumulatedDamage());
            //irons_spellbooks.LOGGER.debug("{} had no spawn immunity", pLivingEntity.getName().getString());

        } else {
            //TODO: find a better way to apply damage
            pLivingEntity.kill(level);
//                serverPlayer.setHealth(serverPlayer.getHealth() - playerMagicData.getSyncedData().getHeartstopAccumulatedDamage());

            //irons_spellbooks.LOGGER.debug("{} had spawn immunity", pLivingEntity.getName().getString());

        }
        playerMagicData.getSyncedData().setHeartstopAccumulatedDamage(0);
    }

    @Override
    public void clientTick(LivingEntity pLivingEntity, MobEffectInstance instance) {
        //irons_spellbooks.LOGGER.debug("{} ticks existed: {}", pLivingEntity.getName().getString(), pLivingEntity.tickCount);

        //Heart beats once every 2 seconds at 0% damage, and 2 times per second at 100% damage (relative to health)
        if (pLivingEntity.level().isClientSide()) {
            if (pLivingEntity instanceof Player player) {
                float damage = ClientMagicData.getSyncedSpellData(player).getHeartstopAccumulatedDamage();
                float f = 1 - Mth.clamp(damage / player.getHealth(), 0, 1);
                int i = (int) (10 + (40 - 10) * f);
                //Ironsspellbooks.logger.debug("{} ({}/{} = {})", i, damage, player.getHealth(), f);
                if (instance.getDuration() % Math.max(i, 1) == 0) {
                    player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1, 0.85f);
                }
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }
}

