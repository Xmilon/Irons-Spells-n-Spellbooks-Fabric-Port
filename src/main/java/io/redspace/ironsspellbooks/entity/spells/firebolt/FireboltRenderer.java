package io.redspace.ironsspellbooks.entity.spells.firebolt;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.Projectile;

public class FireboltRenderer extends EntityRenderer<Projectile, EntityRenderState> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "firebolt_model"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/firebolt.png");

    public FireboltRenderer(Context context) {
        super(context);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 20, 10);
    }

    @Override
    public EntityRenderState createRenderState() {
        // Firebolt is drawn by its particles; no model needs to be submitted.
        return new EntityRenderState();
    }

    public Identifier getTextureLocation(Projectile entity) {
        return TEXTURE;
    }
}
