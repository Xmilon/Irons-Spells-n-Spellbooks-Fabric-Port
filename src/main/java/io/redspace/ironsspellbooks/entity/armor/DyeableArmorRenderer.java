package io.redspace.ironsspellbooks.entity.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.item.Item;
import com.geckolib.animatable.GeoItem;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;

public class DyeableArmorRenderer<T extends Item & GeoItem> extends GenericCustomArmorRenderer<T> {
    public DyeableArmorRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        color = 0xFFFFFFFF; // todo: default color?
        if (bone.getName().startsWith("dye") && this.currentStack != null) {
            color = 0xFFFFFFFF;
        }
        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, color);
    }
}
