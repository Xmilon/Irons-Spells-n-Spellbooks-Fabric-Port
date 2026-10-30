package io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron;

import com.mojang.serialization.Codec;
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
 * Recipe Type for putting liquids into the cauldron (filling cauldron)
 */
// the returned stack is a template: recipes load before item components are bound
public record FillAlchemistCauldronRecipe(Ingredient input, net.minecraft.world.item.ItemStackTemplate returnedItem,
                                          FluidStack result, boolean mustFitAll,
                                          Holder<SoundEvent> fillSound) implements Recipe<SingleRecipeInput> {

    @Override
    public FluidStack result() {
        return result.copy();
    }

    public ItemStack returned() {
        return returnedItem.create();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return returnedItem.create();
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
    public RecipeSerializer<FillAlchemistCauldronRecipe> getSerializer() {
        return RecipeRegistry.ALCHEMIST_CAULDRON_FILL_SERIALIZER.get();
    }

    @Override
    public RecipeType<FillAlchemistCauldronRecipe> getType() {
        return RecipeRegistry.ALCHEMIST_CAULDRON_FILL_TYPE.get();
    }

    public static final class Serializer {
        private Serializer() {}

        public static final MapCodec<FillAlchemistCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Ingredient.CODEC.fieldOf("input").forGetter(FillAlchemistCauldronRecipe::input),
                net.minecraft.world.item.ItemStackTemplate.CODEC.fieldOf("result").forGetter(FillAlchemistCauldronRecipe::returnedItem),
                FluidStack.CODEC.fieldOf("fluid").forGetter(FillAlchemistCauldronRecipe::result),
                Codec.BOOL.optionalFieldOf("mustFitAll", true).forGetter(FillAlchemistCauldronRecipe::mustFitAll),
                BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("sound", BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BOTTLE_EMPTY)).forGetter(FillAlchemistCauldronRecipe::fillSound)
        ).apply(builder, FillAlchemistCauldronRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, FillAlchemistCauldronRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, FillAlchemistCauldronRecipe::input,
                net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC, FillAlchemistCauldronRecipe::returnedItem,
                FluidStack.STREAM_CODEC, FillAlchemistCauldronRecipe::result,
                ByteBufCodecs.BOOL, FillAlchemistCauldronRecipe::mustFitAll,
                ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT), FillAlchemistCauldronRecipe::fillSound,
                FillAlchemistCauldronRecipe::new
        );
    }

    public static class Builder implements RecipeBuilder {

        SoundEvent soundEvent = SoundEvents.BOTTLE_EMPTY;
        Ingredient input = null;
        ItemStack returned = null;
        FluidStack fluid = null;
        boolean mustFitAll = true;

        public Builder withInput(Item input) {
            this.input = Ingredient.of(input);
            return this;
        }

        public Builder withInput(Ingredient input){
            this.input = input;
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

        public Builder mustFitAll(boolean mustFitAll) {
            this.mustFitAll = mustFitAll;
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
            Item firstInput = input.items().findFirst().orElseThrow(() -> new IllegalStateException("Cauldron fill recipe needs an input item")).value();
            return ResourceKey.create(Registries.RECIPE, IronsSpellbooks.id("alchemist_cauldron/fill_" + BuiltInRegistries.ITEM.getKey(firstInput).getPath()));
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
            recipeOutput.accept(id, new FillAlchemistCauldronRecipe(input, net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(returned), fluid, mustFitAll, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent)), null);
        }
    }
}


