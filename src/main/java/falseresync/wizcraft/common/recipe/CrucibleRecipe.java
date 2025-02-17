package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.*;
import net.minecraft.registry.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

public record CrucibleRecipe(ItemStack result, DefaultedList<Ingredient> ingredients) implements Recipe<RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, World world) {
        var inputStacksCount = input.getSize();
        if (inputStacksCount < ingredients.size()) {
            return false;
        }

        int nonEmptySize = 0;
        for (int i = 0; i < inputStacksCount; i++) {
            nonEmptySize += input.getStackInSlot(i).isEmpty() ? 0 : 1;
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
                if (ingredient.test(input.getStackInSlot(i))) {
                    matchedSlots.add(i);
                    break;
                }
            }
        }
        return matchedSlots.size() == nonEmptySize;
    }

    @Override
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return result.copy();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
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
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public static final PacketCodec<RegistryByteBuf, CrucibleRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC, CrucibleRecipe::result,
                PacketCodecs.collection(DefaultedList::ofSize, Ingredient.PACKET_CODEC), CrucibleRecipe::ingredients,
                CrucibleRecipe::new
        );
        private static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(CrucibleRecipe::result),
                                Ingredient.DISALLOW_EMPTY_CODEC
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
                                                        return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, nonEmptyIngredients));
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
        public PacketCodec<RegistryByteBuf, CrucibleRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
