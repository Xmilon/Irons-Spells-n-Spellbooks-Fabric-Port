package io.redspace.ironsspellbooks.entity.spells.comet;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.Projectile;

public class CometRenderer extends FireballRenderer {
    private final static Identifier BASE_TEXTURE = IronsSpellbooks.id("textures/entity/comet/comet.png");
    private final static Identifier FIRE_TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/comet/fire_1.png"),
            IronsSpellbooks.id("textures/entity/comet/fire_2.png"),
            IronsSpellbooks.id("textures/entity/comet/fire_3.png"),
            IronsSpellbooks.id("textures/entity/comet/fire_4.png")
    };

    public CometRenderer(Context context, float scale) {
        super(context, scale);
    }

    @Override
    public Identifier getTextureLocation(Projectile entity) {
        return BASE_TEXTURE;
    }

    public Identifier getFireTextureLocation(Projectile entity) {
        int frame = (entity.tickCount / 2) % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }

}
