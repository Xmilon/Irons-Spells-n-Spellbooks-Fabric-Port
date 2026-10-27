package io.redspace.ironsspellbooks.entity.armor.pumpkin;

import com.geckolib.model.GeoModel;
import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.item.armor.PumpkinArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

public class PumpkinArmorRenderer extends GenericCustomArmorRenderer<PumpkinArmorItem> {
    public PumpkinArmorRenderer(GeoModel<PumpkinArmorItem> model) {
        super(model);
        asyncBones.add(new AsyncBone("armorBodyHeadLayer", EquipmentSlot.CHEST, m -> m.head, Vec3.ZERO));
    }
}
