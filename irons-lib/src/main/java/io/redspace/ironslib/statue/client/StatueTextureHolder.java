package io.redspace.ironslib.statue.client;

import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.statue.PlayerStatueModelType;
import net.minecraft.resources.Identifier;

public record StatueTextureHolder(PatreonPermissions permissions, Identifier textureLocation, PlayerStatueModelType modelType) {
}
