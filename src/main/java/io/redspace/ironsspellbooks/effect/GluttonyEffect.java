package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectCategory;


public class GluttonyEffect extends MagicMobEffect {
    public GluttonyEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static void finishEating(net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack item) {
        //This won't account for cake blocks, but that would require more invasive mixins to detect so this is fine
        if (!entity.level().isClientSide()) {
            var food = item.get(DataComponents.FOOD);
            if (food != null) {
                var gluttony = entity.getEffect(MobEffectRegistry.GLUTTONY);
                if (gluttony != null) {
                    var pmg = MagicData.getPlayerMagicData(entity);
                    pmg.addMana(food.nutrition() * ratioForAmplifier(gluttony.getAmplifier()));
                    if (entity instanceof ServerPlayer serverPlayer) {
                        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(serverPlayer, new SyncManaPacket(pmg, serverPlayer));
                    }
                }
            }
        }
    }

    public static float ratioForAmplifier(int amplifier) {
        return (4 + amplifier) * .5f;
    }
}


