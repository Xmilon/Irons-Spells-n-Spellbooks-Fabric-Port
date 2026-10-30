package io.redspace.ironsspellbooks.entity.mobs;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.renderer.layer.builtin.ItemArmorGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import io.redspace.ironsspellbooks.util.DefaultBipedBoneIdents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.tags.ItemTags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Humanoid GeckoLib renderer: worn armor and held items attach to the standard biped bones.
 * Known gap: the hard-coded cape layer is not ported yet (see ArmorCapeLayer).
 */
public class HumanoidRenderer<T extends Mob & GeoAnimatable> extends IronsGeoEntityRenderer<T> {
    private static final String LEFT_HAND = DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT;
    private static final String RIGHT_HAND = DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT;
    private static final String TORSO = "torso";

    public HumanoidRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        withRenderLayer(new ItemArmorGeoLayer<T, Void, EntityRenderState>(this, context) {
            @Override
            protected List<RenderData> getRelevantBones(RenderPassInfo<EntityRenderState> info) {
                // models only have some of these bones (the second leg/foot sets are optional); GeckoLib 5 logs an error per frame for each missing one
                return Stream.of(
                        RenderData.head(DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT),
                        RenderData.body(DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT),
                        RenderData.leftArm(DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT),
                        RenderData.rightArm(DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT),
                        RenderData.leftLeg(DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT),
                        RenderData.leftLeg(DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT),
                        RenderData.rightLeg(DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT),
                        RenderData.rightLeg(DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT),
                        RenderData.leftFoot(DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT),
                        RenderData.leftFoot(DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT),
                        RenderData.rightFoot(DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT),
                        RenderData.rightFoot(DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT))
                        .filter(data -> info.model().getBone(data.boneName()).isPresent())
                        .toList();
            }
        });

        withRenderLayer(new BlockAndItemGeoLayer<T, Void, EntityRenderState>(context, this) {
            @Override
            protected List<RenderData> getRelevantBones(T animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
                List<RenderData> bones = new ArrayList<>(3);
                addItem(bones, animatable, LEFT_HAND, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
                addItem(bones, animatable, RIGHT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
                addItem(bones, animatable, TORSO, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
                return bones;
            }

            private void addItem(List<RenderData> bones, T animatable, String bone, ItemDisplayContext context) {
                ItemStack stack = stackForBone(animatable, bone);
                if (stack != null && !stack.isEmpty()) {
                    var state = new ItemStackRenderState();
                    this.itemModelResolver.updateForLiving(state, stack, context, animatable);
                    bones.add(RenderData.item(bone, context, state));
                }
            }

            @Override
            public void addRenderData(T animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState itemRenderState, ItemDisplayContext displayContext, EntityRenderState renderState, SubmitNodeCollector collector, int packedLight) {
                T animatable = entity(renderState);
                ItemStack stack = stackForBone(animatable, bone.name());
                poseStack.pushPose();
                poseStack.translate(0, 0, -0.0625);
                poseStack.translate(0, -0.0625, 0);
                boolean offhand = stack == animatable.getOffhandItem();
                poseStack.rotateDegrees(Axis.XP, -90f);
                if (stack != null && stack.getItem() instanceof ShieldItem) {
                    poseStack.translate(0, 0.125, offhand ? 0.25 : -0.25);
                    if (offhand) {
                        poseStack.rotateDegrees(Axis.YP, 180);
                    }
                }
                if (animatable instanceof AbstractSpellCastingMob mob && bone.name().equals(TORSO) && shouldWeaponBeSheathed(mob)) {
                    float hipOffset = animatable.getItemBySlot(EquipmentSlot.CHEST).isEmpty() ? .25f : .325f;
                    poseStack.translate(animatable.isLeftHanded() ? hipOffset : -hipOffset, 0, -.4);
                    poseStack.rotateDegrees(Axis.XP, 215f);
                    poseStack.scale(.85f, .85f, .85f);
                }
                adjustHandItemRendering(poseStack, stack, animatable, renderState.getGeckolibData(PARTIAL_TICK), offhand);
                super.submitItemStackRender(poseStack, bone, itemRenderState, displayContext, renderState, collector, packedLight);
                poseStack.popPose();
            }
        });
    }

    protected ItemStack stackForBone(T animatable, String bone) {
        if (animatable instanceof AbstractSpellCastingMob castingMob) {
            if (isBoneMainHand(castingMob, bone)) {
                if (castingMob.isDrinkingPotion()) {
                    return AbstractSpellCastingMobRenderer.makePotion(castingMob);
                }
                if (shouldWeaponBeSheathed(castingMob) && castingMob.getItemBySlot(EquipmentSlot.MAINHAND).is(ItemTags.SWORDS)) {
                    return ItemStack.EMPTY;
                }
            }
            if (bone.equals(TORSO)) {
                if (shouldWeaponBeSheathed(castingMob) && castingMob.getItemBySlot(EquipmentSlot.MAINHAND).is(ItemTags.SWORDS)) {
                    return castingMob.getItemBySlot(EquipmentSlot.MAINHAND);
                }
                return null;
            }
        }
        return switch (bone) {
            case LEFT_HAND -> animatable.isLeftHanded() ? animatable.getMainHandItem() : animatable.getOffhandItem();
            case RIGHT_HAND -> animatable.isLeftHanded() ? animatable.getOffhandItem() : animatable.getMainHandItem();
            default -> null;
        };
    }

    protected boolean isBoneMainHand(AbstractSpellCastingMob entity, String boneName) {
        return entity.isLeftHanded() && boneName.equals(DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT) || !entity.isLeftHanded() && boneName.equals(DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT);
    }

    protected boolean shouldWeaponBeSheathed(AbstractSpellCastingMob entity) {
        return entity.shouldSheathSword() && !entity.isAggressive();
    }

    protected void adjustHandItemRendering(PoseStack poseStack, ItemStack stack, T animatable, float partialTick, boolean offhand) {

    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        var animatable = entity(state);
        return animatable.isInvisible() ? RenderTypes.entityTranslucentCull(texture) : RenderTypes.entityCutout(texture, false);
    }
}
