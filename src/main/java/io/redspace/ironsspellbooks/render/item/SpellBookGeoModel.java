package io.redspace.ironsspellbooks.render.item;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SpellBookGeoModel extends GeoModel<SpellBook> {
    private static final Identifier MODEL = IronsSpellbooks.id("geo/spell_book.geo.json");
    private static final Identifier ANIMATION = IronsSpellbooks.id("animations/spell_book.animation.json");
    private static final Identifier TEXTURE = IronsSpellbooks.id("textures/item/temp_spellbook.png");

    @Override
    public Identifier getModelResource(SpellBook animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(SpellBook animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(SpellBook animatable) {
        return ANIMATION;
    }
}
