package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.item.ILecternPlaceable;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Everything needed to draw one of our lectern books, captured while the lectern block entity is extracted.
 * A simple texture override is drawn as the vanilla book model; otherwise item is drawn as a 3D item (null draws nothing).
 */
public record LecternBook(ILecternPlaceable placeable, Optional<Identifier> textureOverride, ItemStackRenderState item) {
    public static final RenderStateDataKey<LecternBook> KEY = RenderStateDataKey.create(() -> "irons_spellbooks:lectern_book");
}
