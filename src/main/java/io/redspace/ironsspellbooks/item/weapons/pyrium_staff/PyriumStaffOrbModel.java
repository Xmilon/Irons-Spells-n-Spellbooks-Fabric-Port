package io.redspace.ironsspellbooks.item.weapons.pyrium_staff;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PyriumStaffOrbModel extends Model<Void> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/item/pyrium_staff_orb.png");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "pyrium_staff_orb"), "main");

    public PyriumStaffOrbModel(ModelPart pRoot) {
        super(pRoot, RenderHelper.CustomerRenderType.PYRIUM_STAFF_ORB);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create()
                .texOffs(12, 16).addBox(1.5F, 1.5F, 1.5F, -3.0F, -3.0F, -3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 8).addBox(1.0F, 1.0F, 1.0F, -2.0F, -2.0F, -2.0F, new CubeDeformation(0.0F))
                .texOffs(4, 3).addBox(0.5F, 0.5F, 0.5F, -1.0F, -1.0F, -1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

}
