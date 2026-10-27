package io.redspace.ironsspellbooks.entity.armor;

import com.geckolib.animatable.GeoItem;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Armor renderer whose extra "async" bones are shown for one equipment slot and follow a part of the wearer's base model.
 * unverified: bone rotation/translation conventions follow the old GeckoLib 4 matchModelPartRot (x and y rotation negated).
 */
public class GenericCustomArmorRenderer<T extends Item & GeoItem> extends GeoArmorRenderer<T, HumanoidRenderState> {
    public static class AsyncBone {
        private final String boneName;
        private final EquipmentSlot itemSlot;
        private final Function<HumanoidModel<?>, ModelPart> partToFollow;
        private final Vec3 partOffset;

        public AsyncBone(String boneName, EquipmentSlot itemSlot, Function<HumanoidModel<?>, ModelPart> partToFollow, Vec3 partOffset) {
            this.boneName = boneName;
            this.itemSlot = itemSlot;
            this.partToFollow = partToFollow;
            this.partOffset = partOffset;
        }
    }

    protected final ArrayList<AsyncBone> asyncBones = new ArrayList<>();

    public GenericCustomArmorRenderer(GeoModel<T> model) {
        super(model);
        asyncBones.add(new AsyncBone("armorLeggingTorsoLayer", EquipmentSlot.LEGS, m -> m.body, Vec3.ZERO));
        asyncBones.add(new AsyncBone("armorTorsoExtensionRightLeg", EquipmentSlot.CHEST, m -> m.rightLeg, new Vec3(2, 12, 0)));
        asyncBones.add(new AsyncBone("armorTorsoExtensionLeftLeg", EquipmentSlot.CHEST, m -> m.leftLeg, new Vec3(-2, 12, 0)));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<HumanoidRenderState> info, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(info, snapshots);
        EquipmentSlot slot = info.renderState().getGeckolibData(CURRENT_SLOT);
        HumanoidModel<?> baseModel = info.renderState().getGeckolibData(BASE_MODEL);
        for (AsyncBone bone : asyncBones) {
            boolean visible = slot == bone.itemSlot;
            snapshots.ifPresent(bone.boneName, snapshot -> {
                snapshot.skipRender(!visible);
                if (visible && baseModel != null) {
                    ModelPart part = bone.partToFollow.apply(baseModel);
                    snapshot.setRotation(-part.xRot, -part.yRot, part.zRot);
                    snapshot.setTranslation((float) bone.partOffset.x + part.x, (float) bone.partOffset.y - part.y, (float) bone.partOffset.z + part.z);
                }
            });
        }
    }
}
