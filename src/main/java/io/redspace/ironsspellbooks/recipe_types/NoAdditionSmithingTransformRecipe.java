package io.redspace.ironsspellbooks.recipe_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.RecipeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleSmithingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.Optional;

public class NoAdditionSmithingTransformRecipe extends SimpleSmithingRecipe {
    final Ingredient template;

    final Ingredient base;

    final ItemStack result;

    public NoAdditionSmithingTransformRecipe(Ingredient template, Ingredient base, ItemStack result) {
        super(new Recipe.CommonInfo(true));
        this.template = template;
        this.base = base;
        this.result = result;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        ItemStack itemstack = input.base().transmuteCopy(this.result.getItem(), this.result.getCount());
        itemstack.applyComponents(this.result.getComponentsPatch());
        return itemstack;
    }

    public Ingredient getTemplate() {
        return template;
    }

    public Ingredient getBase() {
        return base;
    }

    public Ingredient getResult() {
        return Ingredient.of(this.result.getItem());
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.of(template);
    }

    @Override
    public Ingredient baseIngredient() {
        return base;
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.empty();
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(List.of(Optional.of(template), Optional.of(base), Optional.empty()));
    }

    @Override
    public RecipeSerializer<NoAdditionSmithingTransformRecipe> getSerializer() {
        return RecipeRegistry.SMITHING_TRANSFORM_NO_ADDITION_SERIALIZER.get();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new SmithingRecipeDisplay(template.display(), base.display(), SlotDisplay.Empty.INSTANCE,
                new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(result)),
                new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
    }

    public static final class Serializer {
        private Serializer() {}

        public static final MapCodec<NoAdditionSmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_340782_ -> p_340782_.group(
                                Ingredient.CODEC.fieldOf("template").forGetter(p_301310_ -> p_301310_.template),
                                Ingredient.CODEC.fieldOf("base").forGetter(p_300938_ -> p_300938_.base),
                                ItemStack.CODEC.validate(ItemStack::validateStrict).fieldOf("result").forGetter(p_300935_ -> p_300935_.result)
                        )
                        .apply(p_340782_, NoAdditionSmithingTransformRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, NoAdditionSmithingTransformRecipe> STREAM_CODEC = StreamCodec.of(
                NoAdditionSmithingTransformRecipe.Serializer::toNetwork, NoAdditionSmithingTransformRecipe.Serializer::fromNetwork
        );

        private static NoAdditionSmithingTransformRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new NoAdditionSmithingTransformRecipe(ingredient, ingredient1, itemstack);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, NoAdditionSmithingTransformRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.template);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.base);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    }
}


