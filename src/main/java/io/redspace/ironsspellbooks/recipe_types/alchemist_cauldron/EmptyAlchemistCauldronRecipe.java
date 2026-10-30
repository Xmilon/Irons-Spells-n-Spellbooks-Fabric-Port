package io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Recipe Type for taking liquids out of the cauldron (emptying cauldron)
 */
public record EmptyAlchemistCauldronRecipe(Ingredient input, ItemStack result,
                                           FluidStack fluid,
                                           Holder<SoundEvent> emptySound) implements Recipe<EmptyAlchemistCauldronRecipe.Input> {
    public record Input(ItemStack item, FluidStack fluid) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return item;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public ItemStack result() {
        return result.copy();
    }

    @Override
    public FluidStack fluid() {
        return fluid.copy();
    }

    @Override
    public boolean matches(EmptyAlchemistCauldronRecipe.Input input, Level level) {
        return this.input.test(input.item()) && input.fluid.getAmount() >= this.fluid.getAmount() && FluidStack.isSameFluidSameComponents(this.fluid, input.fluid);
    }

    @Override
    public ItemStack assemble(EmptyAlchemistCauldronRecipe.Input input) {
        return result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<EmptyAlchemistCauldronRecipe> getSerializer() {
        return RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_SERIALIZER.get();
    }

    @Override
    public RecipeType<EmptyAlchemistCauldronRecipe> getType() {
        return RecipeRegistry.ALCHEMIST_CAULDRON_EMPTY_TYPE.get();
    }

    public static final class Serializer {
        private Serializer() {}

        public static final MapCodec<EmptyAlchemistCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Ingredient.CODEC.fieldOf("input").forGetter(EmptyAlchemistCauldronRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(EmptyAlchemistCauldronRecipe::result),
                FluidStack.CODEC.fieldOf("fluid").forGetter(EmptyAlchemistCauldronRecipe::fluid),
                BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("sound", BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BOTTLE_FILL)).forGetter(EmptyAlchemistCauldronRecipe::emptySound)
        ).apply(builder, EmptyAlchemistCauldronRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, EmptyAlchemistCauldronRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, EmptyAlchemistCauldronRecipe::input,
                ItemStack.STREAM_CODEC, EmptyAlchemistCauldronRecipe::result,
                FluidStack.STREAM_CODEC, EmptyAlchemistCauldronRecipe::fluid,
                ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), EmptyAlchemistCauldronRecipe::emptySound,
                EmptyAlchemistCauldronRecipe::new
        );
    }

    public static class Builder implements RecipeBuilder {

        SoundEvent soundEvent = SoundEvents.BOTTLE_FILL;
        Ingredient input = null;
        ItemStack returned = null;
        FluidStack fluid = null;

        public Builder withInput(Item input) {
            this.input = Ingredient.of(input);
            return this;
        }

        public Builder withReturnItem(Item returned) {
            this.returned = new ItemStack(returned);
            return this;
        }

        public Builder withFluid(Holder<Fluid> fluid, int amount) {
            return withFluid(new FluidStack(fluid, amount));
        }

        public Builder withSound(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public Builder withFluid(FluidStack fluidStack) {
            this.fluid = fluidStack;
            return this;
        }

        @Override
        public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String groupName) {
            return this;
        }

        @Override
        public ResourceKey<Recipe<?>> defaultId() {
            return ResourceKey.create(Registries.RECIPE, IronsSpellbooks.id("alchemist_cauldron/empty_" + BuiltInRegistries.ITEM.getKey(returned.getItem()).getPath()));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
            recipeOutput.accept(id, new EmptyAlchemistCauldronRecipe(input, returned, fluid, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent)), null);
        }
    }
}


