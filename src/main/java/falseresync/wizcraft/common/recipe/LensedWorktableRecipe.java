package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import falseresync.wizcraft.common.CommonKeys;
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

import java.util.ArrayDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class LensedWorktableRecipe implements Recipe<RecipeInput> {
    private final ItemStack result;
    private final int craftingTime;
    private final Ingredient worktableInput;
    private final NonNullList<Ingredient> pedestalInputs;
    private final NonNullList<Ingredient> allIngredients;

    public LensedWorktableRecipe(ItemStack result, Ingredient worktableInput, NonNullList<Ingredient> pedestalInputs) {
        this(result, 100, worktableInput, pedestalInputs);
    }

    public LensedWorktableRecipe(ItemStack result, int craftingTime, Ingredient worktableInput, NonNullList<Ingredient> pedestalInputs) {
        this.result = result;
        this.craftingTime = craftingTime;
        this.worktableInput = worktableInput;
        this.pedestalInputs = pedestalInputs;
        this.allIngredients = NonNullList.createWithCapacity(pedestalInputs.size() + 1);
        this.allIngredients.add(worktableInput);
        this.allIngredients.addAll(pedestalInputs);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return allIngredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput input, Level world) {
        if (input.size() < allIngredients.size()) {
            return false;
        }

        if (!worktableInput.test(input.getItem(0))) {
            return false;
        }

        var stacksInSlots = IntStream.rangeClosed(1, input.size())
                .mapToObj(input::getItem)
                .filter(stack -> !stack.isEmpty())
                .collect(Collectors.toCollection(ArrayDeque::new));
        // These loops are meant to "rotate" the stacks around until they match pedestal inputs
        for (int i = 0; i < stacksInSlots.size(); i++) {
            var stacksMatchPedestals = true;
            for (var pedestalInput : this.pedestalInputs) {
                var currentStack = stacksInSlots.removeFirst();
                stacksInSlots.offerLast(currentStack);
                if (!pedestalInput.test(currentStack)) {
                    stacksMatchPedestals = false;
                    break;
                }
            }
            if (stacksMatchPedestals) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= allIngredients.size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<LensedWorktableRecipe> getSerializer() {
        return WizcraftRecipeSerializers.LENSED_WORKTABLE;
    }

    @Override
    public RecipeType<LensedWorktableRecipe> getType() {
        return WizcraftRecipes.LENSED_WORKTABLE;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public Ingredient getWorktableInput() {
        return worktableInput;
    }

    public NonNullList<Ingredient> getPedestalInputs() {
        return pedestalInputs;
    }

    public static class Serializer implements RecipeSerializer<LensedWorktableRecipe> {
        public static final MapCodec<LensedWorktableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf(CommonKeys.RESULT).forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf(CommonKeys.CRAFTING_TIME, 100)
                        .flatXmap(
                                craftingTime -> craftingTime < 30
                                        ? DataResult.error(() -> "Crafting time below 30 ticks is unsupported")
                                        : DataResult.success(craftingTime),
                                DataResult::success
                        )
                        .forGetter(recipe -> recipe.craftingTime),
                Ingredient.CODEC_NONEMPTY.fieldOf(CommonKeys.WORKTABLE).forGetter(recipe -> recipe.worktableInput),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf(CommonKeys.PEDESTALS)
                        .flatXmap(
                                ingredients -> {
                                    var nonEmptyIngredients = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                    if (nonEmptyIngredients.length == 0) {
                                        return DataResult.error(() -> "No pedestal ingredients for a Lensed worktable recipe");
                                    } else {
                                        return nonEmptyIngredients.length > 4
                                                ? DataResult.error(() -> "Too many pedestal ingredients for a Lensed worktable recipe")
                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, nonEmptyIngredients));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(recipe -> recipe.pedestalInputs)
        ).apply(instance, LensedWorktableRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, LensedWorktableRecipe> PACKET_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, recipe -> recipe.result,
                ByteBufCodecs.INT, recipe -> recipe.craftingTime,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.worktableInput,
                ByteBufCodecs.collection(NonNullList::createWithCapacity, Ingredient.CONTENTS_STREAM_CODEC), recipe -> recipe.pedestalInputs,
                LensedWorktableRecipe::new
        );

        @Override
        public MapCodec<LensedWorktableRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LensedWorktableRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}
