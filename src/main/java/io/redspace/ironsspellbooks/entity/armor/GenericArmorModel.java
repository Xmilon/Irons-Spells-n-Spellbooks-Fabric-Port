package io.redspace.ironsspellbooks.entity.armor;

import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoArmorRenderer;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;
import java.util.Map;

public class GenericArmorModel<T extends ExtendedArmorItem> extends DefaultedItemGeoModel<T> {
    private final Identifier model;

    private final Identifier texture;
    private Map<String, Identifier> modelVariants = Map.of();
    private static final DataTicket<String> CLOTHING_VARIANT = DataTicket.create("irons_spellbooks:clothing_variant", String.class);

    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "wizard_armor_animation");

    public GenericArmorModel(String modid, String name) {
        super(Identifier.fromNamespaceAndPath(modid, name + "_armor"));
        this.model = Identifier.fromNamespaceAndPath(modid, name + "_armor");
        this.texture = Identifier.fromNamespaceAndPath(modid, String.format("textures/models/armor/%s.png", name));
    }

    public GenericArmorModel(String name) {
        this(IronsSpellbooks.MODID, name);
    }

    public GenericArmorModel<T> variants(Map<String, Identifier> modelVariants) {
        this.modelVariants = Map.copyOf(modelVariants);
        return this;
    }

    @Override
    public void addAdditionalStateData(T animatable, Object relatedObject, GeoRenderState state) {
        super.addAdditionalStateData(animatable, relatedObject, state);
        String variant = relatedObject instanceof GeoArmorRenderer.RenderData data
                ? data.itemStack().getOrDefault(ComponentRegistry.CLOTHING_VARIANT, "") : "";
        state.addGeckolibData(CLOTHING_VARIANT, variant);
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return modelVariants.getOrDefault(state.getOrDefaultGeckolibData(CLOTHING_VARIANT, ""), model);
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return ANIMATION;
    }
}
