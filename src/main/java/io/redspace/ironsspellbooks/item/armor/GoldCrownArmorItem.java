package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.renderer.GeoArmorRenderer;

public class GoldCrownArmorItem extends ExtendedArmorItem {
    public GoldCrownArmorItem(ArmorType slot, Properties settings) {
        super(ArmorMaterialRegistry.DEV, slot, settings,
                new AttributeContainer(AttributeRegistry.MAX_MANA, 9900, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.90, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
