package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.renderer.GeoArmorRenderer;

public class PyromancerArmorItem extends ImbuableChestplateArmorItem implements IArmorCapeProvider {
    public PyromancerArmorItem(Type slot, Properties settings) {
        super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.FIRE_SPELL_POWER));
    }

    @Override
    public Identifier getCapeIdentifier() {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/pyromancer_cape.png");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
