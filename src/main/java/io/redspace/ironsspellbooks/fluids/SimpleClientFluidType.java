package io.redspace.ironsspellbooks.fluids;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class SimpleClientFluidType implements IClientFluidTypeExtensions {

    private final Identifier texture;

    public SimpleClientFluidType(Identifier texture) {
        this.texture = texture;
    }


    public @NotNull Identifier getStillTexture() {
        return texture;
    }


    public @NotNull Identifier getFlowingTexture() {
        return texture;
    }

}
