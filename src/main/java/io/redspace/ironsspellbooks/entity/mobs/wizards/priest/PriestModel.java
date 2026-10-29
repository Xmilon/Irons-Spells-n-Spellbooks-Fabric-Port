package io.redspace.ironsspellbooks.entity.mobs.wizards.priest;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;

public class PriestModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/priest/priest.png");
    public static final Identifier TEXTURE_ARMOR = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/priest/priest_armored.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "archevoker");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        var object = mob(state);
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        return object.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.PRIEST_HELMET.get()) ? TEXTURE_ARMOR : TEXTURE;
    }

    @Override
    public void animateBones(AbstractSpellCastingMob entity, float partialTick, TransformStack transformStack) {
        this.transformStack = transformStack;
        super.animateBones(entity, partialTick, transformStack);
        if (entity instanceof PriestEntity priest && priest.isUnhappy()) {
            if (Minecraft.getInstance().isPaused() || !entity.shouldBeExtraAnimated())
                return;
            String head = PartNames.HEAD;
            transformStack.setRotZ(head, 0.3F * Mth.sin(0.45F * (entity.tickCount + partialTick)));
            transformStack.setRotX(head, -0.4F);
        }
    }
}
