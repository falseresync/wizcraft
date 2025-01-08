package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MortarAndPestleRecipe implements CraftingRecipe {
    protected final ItemStack result;
    protected final DefaultedList<Ingredient> ingredients;
    protected final DefaultedList<Ingredient> virtualIngredients;

    public MortarAndPestleRecipe(ItemStack result, DefaultedList<Ingredient> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
        virtualIngredients = DefaultedList.ofSize(ingredients.size() + 2);
        virtualIngredients.addAll(ingredients);
        virtualIngredients.addFirst(Ingredient.ofItems(Items.FLINT));
        virtualIngredients.addLast(Ingredient.ofItems(Items.BOWL));
    }

    @Override
    public RecipeSerializer<MortarAndPestleRecipe> getSerializer() {
        return WizcraftRecipeSerializers.MORTAR_AND_PESTLE;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.MISC;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.virtualIngredients;
    }

    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        var inputStacksCount = craftingRecipeInput.getStackCount();
        if (inputStacksCount != virtualIngredients.size()) {
            return false;
        } else {
            var matchedSlots = new IntArraySet();
            for (Ingredient ingredient : new ArrayList<>(virtualIngredients)) {
                for (int i = 0; i < inputStacksCount; i++) {
                    if (matchedSlots.contains(i)) {
                        continue;
                    }
                    if (ingredient.test(craftingRecipeInput.getStackInSlot(i))) {
                        matchedSlots.add(i);
                        break;
                    }
                }
            }
            return matchedSlots.size() == inputStacksCount;
        }
    }

    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= this.virtualIngredients.size();
    }

    public static class Serializer implements RecipeSerializer<MortarAndPestleRecipe> {
        private static final MapCodec<MortarAndPestleRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Ingredient.DISALLOW_EMPTY_CODEC
                                        .listOf()
                                        .fieldOf("ingredients")
                                        .flatXmap(
                                                ingredients -> {
                                                    Ingredient[] nonEmptyIngredients = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                                    if (nonEmptyIngredients.length == 0) {
                                                        return DataResult.error(() -> "No ingredients for mortar and pestle recipe");
                                                    } else if (nonEmptyIngredients.length > 7) {
                                                        return DataResult.error(() -> "Too many ingredients for mortar and pestle recipe");
                                                    } else {
                                                        return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, nonEmptyIngredients));
                                                    }
                                                },
                                                DataResult::success
                                        )
                                        .forGetter(recipe -> recipe.ingredients)
                        )
                        .apply(instance, MortarAndPestleRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, MortarAndPestleRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC, recipe -> recipe.result,
                PacketCodecs.collection(DefaultedList::ofSize, Ingredient.PACKET_CODEC), recipe -> recipe.ingredients,
                MortarAndPestleRecipe::new
        );

        @Override
        public MapCodec<MortarAndPestleRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MortarAndPestleRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
