package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.EmptyAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.FillAlchemistCauldronRecipe;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import java.io.File;
import java.io.FileWriter;

public class CreateRecipeCompatGenerator {


    public static int run(CommandContext<CommandSourceStack> context) {
        RecipeManager recipeManager = context.getSource().getServer().getRecipeManager();
        var registryOps = RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());
        new File("create_compat").mkdir();
        for (var recipeHolder : recipeManager.getRecipes()) {
            String path = recipeHolder.id().identifier().getPath();
            if (recipeHolder.value() instanceof EmptyAlchemistCauldronRecipe recipe) {
                Identifier resultId = BuiltInRegistries.ITEM.getKey(recipe.result().getItem());
                if (resultId.getNamespace().equals(IronsSpellbooks.MODID)) {
                    String stringJson = String.format(FILL_FORMAT,
                            Ingredient.CODEC.encodeStart(registryOps, recipe.input()).getOrThrow(),
                            BuiltInRegistries.FLUID.getKey(recipe.fluid().getFluid()),
                            recipe.fluid().getAmount(), resultId);
                    writeRecipe("create_compat/create_fill_" + path.replaceFirst("^alchemist_cauldron/empty_", "").replace('/', '_') + ".json", stringJson);
                }
            } else if (recipeHolder.value() instanceof FillAlchemistCauldronRecipe recipe) {
                Identifier fluidId = BuiltInRegistries.FLUID.getKey(recipe.result().getFluid());
                if (fluidId.getNamespace().equals(IronsSpellbooks.MODID)) {
                    String stringJson = String.format(EMPTY_FORMAT,
                            Ingredient.CODEC.encodeStart(registryOps, recipe.input()).getOrThrow(),
                            BuiltInRegistries.ITEM.getKey(recipe.returned().getItem()),
                            fluidId, recipe.result().getAmount());
                    writeRecipe("create_compat/create_empty_" + path.replaceFirst("^alchemist_cauldron/fill_", "").replace('/', '_') + ".json", stringJson);
                }
            }
        }
        return 1;
    }

    private static void writeRecipe(String path, String json) {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json);
        } catch (Exception e) {
            IronsSpellbooks.LOGGER.error("Failed to generate recipe {}", path, e);
        }
    }
    private static final String EMPTY_FORMAT =
            """
                    {
                      "neoforge:conditions": [
                        {
                          "type": "neoforge:mod_loaded",
                          "modid": "create"
                        }
                      ],
                      "type": "create:emptying",
                      "ingredients": [
                        %s
                      ],
                      "results": [
                        {
                          "id": "%s"
                        },
                        {
                          "id": "%s",
                          "amount": %s
                        }
                      ]
                    }
                    """;
    private static final String FILL_FORMAT =
            """
                    {
                      "neoforge:conditions": [
                        {
                          "type": "neoforge:mod_loaded",
                          "modid": "create"
                        }
                      ],
                      "type": "create:filling",
                      "ingredients": [
                        %s,
                        {
                          "type": "fluid_stack",
                          "fluid": "%s",
                          "amount": %s
                        }
                      ],
                      "results": [
                        {
                          "id": "%s"
                        }
                      ]
                    }
                    """;
}
