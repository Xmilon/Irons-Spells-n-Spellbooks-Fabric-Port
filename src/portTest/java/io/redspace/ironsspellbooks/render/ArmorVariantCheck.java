package io.redspace.ironsspellbooks.render;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import io.redspace.ironsspellbooks.entity.armor.GenericArmorModel;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import net.minecraft.resources.Identifier;
import java.util.HashMap;
import java.util.Map;

public class ArmorVariantCheck {
    public static void main(String[] args) {
        var variants = new HashMap<String, Identifier>();
        var hat = Identifier.parse("example:wizard_hat");
        variants.put("hat", hat);
        var model = new GenericArmorModel<ExtendedArmorItem>("example", "wizard").variants(variants);
        var state = new GeoRenderState() {
            private final Map<DataTicket<?>, Object> values = new HashMap<>();
            public Map<DataTicket<?>, Object> getDataMap() { return values; }
        };
        var ticket = DataTicket.create("irons_spellbooks:clothing_variant", String.class);
        var base = Identifier.parse("example:wizard_armor");
        assert model.getModelResource(state).equals(base);
        state.addGeckolibData(ticket, "hat");
        variants.clear();
        assert model.getModelResource(state).equals(hat) : "Variant map retained caller mutation";
        state.addGeckolibData(ticket, "unknown");
        assert model.getModelResource(state).equals(base) : "Unknown variant lost default model";
        state.addGeckolibData(ticket, "hat");
        model.addAdditionalStateData(null, null, state);
        assert model.getModelResource(state).equals(base) : "Reused state retained stale clothing variant";
        assert model.getAnimationResource(null).equals(Identifier.parse("irons_spellbooks:wizard_armor_animation"));
        assert model.getTextureResource(state).equals(Identifier.parse("example:textures/models/armor/wizard.png"));
        System.out.println("Armor variant defaults, namespace, immutable map and stale-state clearing passed");
    }
}
