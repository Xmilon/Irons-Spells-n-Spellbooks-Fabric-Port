package io.redspace.ironsspellbooks.render.animation;

import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.ModifierLayer;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.PlayState;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

import static io.redspace.ironsspellbooks.config.ClientConfigs.SHOW_FIRST_PERSON_ARMS;
import static io.redspace.ironsspellbooks.config.ClientConfigs.SHOW_FIRST_PERSON_ITEMS;

public class AnimationHelper {
    public static void initializePlayerAnimationFactory() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                SpellAnimations.ANIMATION_RESOURCE, 42, CastAnimationLayer::new);
    }

    public static void cancelPlayerAnimation(Player player) {
        if (PlayerAnimationAccess.getPlayerAnimationLayer(player, SpellAnimations.ANIMATION_RESOURCE) instanceof CastAnimationLayer layer) {
            layer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(4, EasingType.EASE_IN_OUT_SINE), null, false);
            layer.adjustment.fadeOut(5);
        }
    }

    public static void animatePlayerStart(Player player, Identifier animationId) {
        var rawAnimation = PlayerAnimResources.getAnimation(animationId);
        if (rawAnimation == null || !(PlayerAnimationAccess.getPlayerAnimationLayer(player, SpellAnimations.ANIMATION_RESOURCE) instanceof CastAnimationLayer layer)) {
            return;
        }
        var animation = new PlayerAnimationController(player, (controller, data, setter) -> PlayState.STOP) {
            private boolean fadingAim;

            @Override
            public void tick(AnimationData data) {
                if (layer.getAnimation() == this && !fadingAim && getCurrentAnimation() != null && getAnimationTicks() >= rawAnimation.length() - 2) {
                    layer.adjustment.fadeOut(3);
                    fadingAim = true;
                }
                super.tick(data);
            }
        };
        animation.triggerAnimation(rawAnimation);
        boolean arms = SHOW_FIRST_PERSON_ARMS.get();
        boolean items = SHOW_FIRST_PERSON_ITEMS.get();
        animation.setFirstPersonMode(arms || items ? FirstPersonMode.THIRD_PERSON_MODEL : FirstPersonMode.DISABLED);
        animation.setFirstPersonConfiguration(new FirstPersonConfiguration(arms, arms, items, items));
        layer.adjustment.resetFadeOut();
        layer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(2, EasingType.EASE_IN_OUT_SINE), animation, true);
    }

    private static final class CastAnimationLayer extends ModifierLayer<PlayerAnimationController> {
        private final IronsAdjustmentModifier adjustment;

        private CastAnimationLayer(Avatar avatar) {
            adjustment = new IronsAdjustmentModifier(avatar, this::getAnimation);
            addModifier(adjustment, 0);
            addModifierLast(new MirrorModifier() {
                @Override
                public void setupAnim(AnimationData data) {
                    enabled = (avatar instanceof Player player && ClientMagicData.getSyncedSpellData(player)
                            .getCastingEquipmentSlot().equals(SpellSelectionManager.OFFHAND)) ^ avatar.getMainArm() == HumanoidArm.LEFT;
                    super.setupAnim(data);
                }
            });
        }
    }
}
