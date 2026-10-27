package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;


import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.render.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.Nullable;

import static io.redspace.ironsspellbooks.render.EnergySwirlLayer.CHARGE_TEXTURE;
import static io.redspace.ironsspellbooks.render.EnergySwirlLayer.EVASION_TEXTURE;

public abstract class AbstractSpellCastingMobRenderer extends HumanoidRenderer<AbstractSpellCastingMob> {
    private Identifier textureResource;

    public AbstractSpellCastingMobRenderer(EntityRendererProvider.Context renderManager, AbstractSpellCastingMobModel model) {
        super(renderManager, model);
        this.shadowRadius = 0.5f;
        //this.addLayer(new GeoEvasionLayer(this));
        withRenderLayer(new EnergySwirlLayer.Geo(this, EVASION_TEXTURE, MobEffectRegistry.EVASION));
        withRenderLayer(new EnergySwirlLayer.Geo(this, CHARGE_TEXTURE, MobEffectRegistry.CHARGED));
        withRenderLayer(new ChargeSpellLayer.Geo(this));
        withRenderLayer(new GlowingEyesLayer.Geo(this));
        withRenderLayer(new SpellTargetingLayer.Geo(this));
        withRenderLayer(new GeoSpinAttackLayer(this));
    }

    public static ItemStack makePotion(AbstractSpellCastingMob entity) {
        ItemStack healthPotion = new ItemStack(Items.POTION);
        return Utils.setPotion(healthPotion, entity.isInvertedHealAndHarm() ? Potions.HARMING : Potions.HEALING);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        var entity = entity(state);
        SpellRenderingHelper.renderSpellHelper(ClientMagicData.getSyncedSpellData(entity), entity, poseStack, collector, state.getGeckolibData(PARTIAL_TICK));
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return entity(state).isInvisible() ? RenderTypes.entityTranslucent(texture) : super.getRenderType(state, texture);
    }
}
