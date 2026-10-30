package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.IArmorCapeProvider;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ArmorCapeLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {
    /** Everything the cape needs from the entity, captured once per extraction. */
    public record CapePose(Identifier texture, float lift, float lean, float twist, boolean crouching) {
    }

    public static final RenderStateDataKey<CapePose> CAPE = RenderStateDataKey.create(() -> "irons_spellbooks:armor_cape");
    public static ModelLayerLocation ARMOR_CAPE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "armor_cape"), "main");
    private static final Map<Integer, IArmorCapeProvider.CapeData> CAPE_DATA = new HashMap<>();

    private final ModelPart cape;

    public ArmorCapeLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
        this.cape = Minecraft.getInstance().getEntityModels().bakeLayer(ARMOR_CAPE_LAYER).getChild("cape");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
                "cape",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static void capture(LivingEntity entity, LivingEntityRenderState state, float partialTick) {
        CapePose pose = null;
        if (shouldRender(entity)) {
            var texture = ((IArmorCapeProvider) entity.getItemBySlot(EquipmentSlot.CHEST).getItem()).getCapeIdentifier();
            var capeData = CAPE_DATA.computeIfAbsent(entity.getId(), id -> new IArmorCapeProvider.CapeData());
            if (capeData.lastTick != entity.tickCount) {
                capeData.moveCloak(entity);
                capeData.lastTick = entity.tickCount;
            }
            double d0 = Mth.lerp((double) partialTick, capeData.xCloakO, capeData.xCloak) - Mth.lerp((double) partialTick, entity.xo, entity.getX());
            double d1 = Mth.lerp((double) partialTick, capeData.yCloakO, capeData.yCloak) - Mth.lerp((double) partialTick, entity.yo, entity.getY());
            double d2 = Mth.lerp((double) partialTick, capeData.zCloakO, capeData.zCloak) - Mth.lerp((double) partialTick, entity.zo, entity.getZ());
            float bodyRot = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
            double d3 = Mth.sin(bodyRot * (float) (Math.PI / 180.0));
            double d4 = -Mth.cos(bodyRot * (float) (Math.PI / 180.0));
            float lift = Mth.clamp((float) d1 * 10.0F, -6.0F, 32.0F);
            float lean = Mth.clamp((float) (d0 * d3 + d2 * d4) * 100.0F, 0.0F, 150.0F);
            float twist = Mth.clamp((float) (d0 * d4 - d2 * d3) * 100.0F, -20.0F, 20.0F);
            float bob = Mth.lerp(partialTick, capeData.oBob, capeData.bob);
            lift += Mth.sin(state.walkAnimationPos * 6.0F) * 32.0F * bob;
            if (entity.isCrouching()) {
                lift += 25.0F;
            }
            pose = new CapePose(texture, lift, lean, twist, entity.isCrouching());
        }
        ((FabricRenderState) state).setData(CAPE, pose);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
        CapePose pose = ((FabricRenderState) state).getData(CAPE);
        if (pose == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.125F);
        this.getParentModel().body.translateAndRotate(poseStack);
        poseStack.rotateDegrees(Axis.XP, 6.0F + pose.lean() / 2.0F + pose.lift());
        poseStack.rotateDegrees(Axis.ZP, pose.twist() / 2.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F - pose.twist() / 2.0F);
        if (pose.crouching()) {
            // the shared cape part is never moved; offset through the pose instead (model units are 1/16 block)
            poseStack.translate(0.0F, 1.85F / 16.0F, 1.4F / 16.0F);
        }
        collector.submitModelPart(this.cape, poseStack, RenderTypes.entityCutout(pose.texture()), packedLight, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
    }

    private static boolean shouldRender(LivingEntity livingEntity) {
        ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        return !itemstack.is(Items.ELYTRA)
                && itemstack.getItem() instanceof IArmorCapeProvider
                && !hasPlayerCape(livingEntity)
                && !livingEntity.hasEffect(MobEffectRegistry.ANGEL_WINGS)
                && !livingEntity.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY);
    }

    private static boolean hasPlayerCape(LivingEntity livingEntity) {
        return livingEntity instanceof AbstractClientPlayer player && player.getSkin().cape() != null;
    }
}
