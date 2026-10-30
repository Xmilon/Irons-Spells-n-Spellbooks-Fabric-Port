package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.WeakHashMap;

public class AngelWingsLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    private static final Identifier WINGS_LOCATION = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/angel_wings.png");
    public static final RenderStateDataKey<AngelWingsModel.Pose> POSE = RenderStateDataKey.create(() -> "irons_spellbooks:angel_wings");
    private static final Map<LivingEntity, FramePose> SMOOTHED_POSES = new WeakHashMap<>();
    private final AngelWingsModel model;

    private record FramePose(int tick, float partialTick, AngelWingsModel.Pose pose) {}

    public AngelWingsLayer(RenderLayerParent<S, M> renderer, EntityRendererProvider.Context context) {
        super(renderer);
        this.model = new AngelWingsModel(context.bakeLayer(AngelWingsModel.ANGEL_WINGS_LAYER));
    }

    public static void capture(LivingEntity entity, LivingEntityRenderState state, float partialTick) {
        AngelWingsModel.Pose pose = AngelWingsModel.Pose.HIDDEN;
        if (!entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) && entity.hasEffect(MobEffectRegistry.ANGEL_WINGS)) {
            pose = AngelWingsModel.targetPose(entity.isFallFlying(), entity.isCrouching(), entity.getDeltaMovement(), entity.isBaby());
            if (entity instanceof AbstractClientPlayer) {
                FramePose previous = SMOOTHED_POSES.get(entity);
                if (previous == null || previous.tick() != entity.tickCount || previous.partialTick() != partialTick) {
                    pose = AngelWingsModel.smooth(previous == null ? AngelWingsModel.Pose.HIDDEN : previous.pose(), pose);
                    SMOOTHED_POSES.put(entity, new FramePose(entity.tickCount, partialTick, pose));
                } else {
                    pose = previous.pose();
                }
            }
        }
        ((FabricRenderState) state).setData(POSE, pose);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
        AngelWingsModel.Pose pose = ((FabricRenderState) state).getDataOrDefault(POSE, AngelWingsModel.Pose.HIDDEN);
        if (!pose.visible()) return;
        poseStack.pushPose();
        poseStack.translate(0, 0, .125f);
        if (pose.baby()) {
            poseStack.scale(.5f, .5f, .5f);
            poseStack.translate(0, 1.5f, 0);
        }
        collector.submitModel(model, pose, poseStack, RenderTypes.energySwirl(WINGS_LOCATION, 0, 0), packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }
}
