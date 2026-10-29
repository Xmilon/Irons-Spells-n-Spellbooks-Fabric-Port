package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffRenderer;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;

public final class ItemModelRegistration {
    private ItemModelRegistration() { }

    public static void register() {
        ItemModels.ID_MAPPER.put(Identifier.fromNamespaceAndPath("irons_spellbooks", "school"), SchoolItemModel.Unbaked.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(Identifier.fromNamespaceAndPath("irons_spellbooks", "pyrium_staff"), PyriumStaffRenderer.Unbaked.CODEC);
    }
}
