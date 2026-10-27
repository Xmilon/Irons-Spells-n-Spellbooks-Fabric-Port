package io.redspace.ironsspellbooks.entity.spells.thrown_spear;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrownSpearRenderer extends EntityBackedRenderer<ThrownSpear> {

    private final ItemModelResolver itemModelResolver;

    public ThrownSpearRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    protected void submit(ThrownSpear entity, float partialTicks, int packedLight, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var item = entity.getWeaponItem();
        if (item.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.XP, 90);
        poseStack.rotateDegrees(Axis.ZP, -Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()));
        poseStack.rotateDegrees(Axis.XP, -Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 10);

        poseStack.translate(0, -1.5, 0);
        var renderState = new ItemStackRenderState();
        itemModelResolver.updateForNonLiving(renderState, item, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
        renderState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
        if (entity.isChanneled()) {
            poseStack.rotateDegrees(Axis.XP, 90-10);
            LightningLanceRenderer.renderModel(poseStack, collector, entity.tickCount);
        }
        poseStack.popPose();
    }

    /**
     * Returns the location of an entity's texture.
     */
    public Identifier getTextureLocation(ThrownSpear entity) {
        return ThrownTridentRenderer.TRIDENT_LOCATION;
    }
}

