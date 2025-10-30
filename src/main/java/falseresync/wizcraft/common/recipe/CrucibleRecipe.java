package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record CrucibleRecipe(ItemStack result, NonNullList<Ingredient> ingredients) implements Recipe<RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, Level world) {
        var inputStacksCount = input.size();
        if (inputStacksCount < ingredients.size()) {
            return false;
        }

        int nonEmptySize = 0;
        for (int i = 0; i < inputStacksCount; i++) {
            nonEmptySize += input.getItem(i).isEmpty() ? 0 : 1;
        }
        if (nonEmptySize < ingredients.size()) {
            return false;
        }

        var matchedSlots = new IntArraySet();
        for (var ingredient : ingredients) {
            for (int i = 0; i < inputStacksCount; i++) {
                if (matchedSlots.contains(i)) {
                    continue;
                }
                if (ingredient.test(input.getItem(i))) {
                    matchedSlots.add(i);
                    break;
                }
            }
        }
        return matchedSlots.size() == nonEmptySize;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<CrucibleRecipe> getSerializer() {
        return WizcraftRecipeSerializers.CRUCIBLE;
    }

    @Override
    public RecipeType<CrucibleRecipe> getType() {
        return WizcraftRecipes.CRUCIBLE;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> PACKET_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, CrucibleRecipe::result,
                ByteBufCodecs.collection(NonNullList::createWithCapacity, Ingredient.CONTENTS_STREAM_CODEC), CrucibleRecipe::ingredients,
                CrucibleRecipe::new
        );
        private static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CrucibleRecipe::result),
                                Ingredient.CODEC_NONEMPTY
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                ingredients -> {
                                                    Ingredient[] nonEmptyIngredients = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                                    if (nonEmptyIngredients.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for crucible recipe");
                                                    } else if (nonEmptyIngredients.length > 5) {
                                                        return DataResult.error(() -> "Too many ingredients crucible recipe");
                                                    } else {
                                                        return DataResult.success(NonNullList.of(Ingredient.EMPTY, nonEmptyIngredients));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(CrucibleRecipe::ingredients)
                        )
                        .apply(instance, CrucibleRecipe::new)
        );

        @Override
        public MapCodec<CrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
