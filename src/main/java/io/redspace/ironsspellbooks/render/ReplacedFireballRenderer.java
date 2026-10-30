package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;

public class ReplacedFireballRenderer extends FireballRenderer {
    ThrownItemRenderer<Fireball> backupRenderer;

    public ReplacedFireballRenderer(EntityRendererProvider.Context context, float scale, float backupScale) {
        super(context, scale);
        backupRenderer = new ThrownItemRenderer<Fireball>(context, backupScale, true);
    }

    @Override
    protected void submit(Projectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (entity instanceof LargeFireball && ClientConfigs.safeGet(ClientConfigs.REPLACE_GHAST_FIREBALL) || entity instanceof SmallFireball && ClientConfigs.safeGet(ClientConfigs.REPLACE_BLAZE_FIREBALL)) {
            super.submit(entity, partialTicks, light, poseStack, collector, camera);
        } else {
            backupRenderer.submit(backupRenderer.createRenderState((Fireball) entity, partialTicks), poseStack, collector, camera);
        }
    }
}
