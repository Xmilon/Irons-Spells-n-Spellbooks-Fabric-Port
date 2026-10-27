package io.redspace.ironsspellbooks.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelIdentifier;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class NBTOverrideItemModel implements BakedModel {
    private final BakedModel original;
    private final ItemOverrides itemOverrides;

    public NBTOverrideItemModel(BakedModel original) {
        this.original = original;
        BlockModel missing = null;
        this.itemOverrides = new ItemOverrides(new ModelBaker() {
            public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
                return null;
            }

            public @Nullable UnbakedModel getTopLevelModel(ModelIdentifier location) {
                return null;
            }

            public @Nullable BakedModel bake(Identifier location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
                return null;
            }

            public @Nullable BakedModel bakeUncached(UnbakedModel model, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
                return null;
            }

            @Override
            public @Nullable UnbakedModel getModel(Identifier resourceLocation) {
                return null;
            }

            @Override
            public @Nullable BakedModel bake(Identifier resourceLocation, ModelState modelState) {
                return null;
            }
        }, missing, Collections.emptyList()) {
            @Override
            public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity livingEntity, int seed) {
                var override = getModelFromStack(itemStack);
                if (override.isPresent()) {
                    var manager = Minecraft.getInstance().getModelManager();
                    var missing = manager.getMissingModel();

                    var standalone = manager.getModel(override.get());
                    if (standalone != missing) {
                        return standalone;
                    }

                    var inventory = manager.getModel(ModelIdentifier.inventory(override.get()));
                    if (inventory != missing) {
                        return inventory;
                    }
                }
                return original;
            }
        };
    }

    abstract Optional<Identifier> getModelFromStack(ItemStack itemStack);

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return itemOverrides;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return original.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return original.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return original.getTransforms();
    }
}
