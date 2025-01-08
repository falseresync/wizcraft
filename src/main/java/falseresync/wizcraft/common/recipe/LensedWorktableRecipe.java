package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import falseresync.wizcraft.common.CommonKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class LensedWorktableRecipe implements Recipe<RecipeInput> {
    private final ItemStack result;
    private final int craftingTime;
    private final Ingredient worktableInput;
    private final DefaultedList<Ingredient> pedestalInputs;
    private final DefaultedList<Ingredient> allIngredients;

    public LensedWorktableRecipe(ItemStack result, Ingredient worktableInput, DefaultedList<Ingredient> pedestalInputs) {
        this(result, 100, worktableInput, pedestalInputs);
    }

    public LensedWorktableRecipe(ItemStack result, int craftingTime, Ingredient worktableInput, DefaultedList<Ingredient> pedestalInputs) {
        this.result = result;
        this.craftingTime = craftingTime;
        this.worktableInput = worktableInput;
        this.pedestalInputs = pedestalInputs;
        this.allIngredients = DefaultedList.ofSize(pedestalInputs.size() + 1);
        this.allIngredients.add(worktableInput);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.allIngredients;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput input, World world) {
        if (input.getSize() < this.allIngredients.size()) {
            return false;
        }

        if (!this.worktableInput.test(input.getStackInSlot(0))) {
            return false;
        }

        var stacksInSlots = IntStream.rangeClosed(1, input.getSize())
                .mapToObj(input::getStackInSlot)
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
    public ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.getResult(lookup).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= this.allIngredients.size();
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return result;
    }

    @Override
    public RecipeSerializer<LensedWorktableRecipe> getSerializer() {
        return WizcraftRecipeSerializers.LENSED_WORKTABLE;
    }

    @Override
    public RecipeType<LensedWorktableRecipe> getType() {
        return WizcraftRecipeTypes.LENSED_WORKTABLE;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public Ingredient getWorktableInput() {
        return this.worktableInput;
    }

    public DefaultedList<Ingredient> getPedestalInputs() {
        return this.pedestalInputs;
    }

    public static class Serializer implements RecipeSerializer<LensedWorktableRecipe> {
        public static final MapCodec<LensedWorktableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.VALIDATED_CODEC.fieldOf(CommonKeys.RESULT).forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf(CommonKeys.CRAFTING_TIME, 100)
                        .flatXmap(
                                craftingTime -> craftingTime < 30
                                        ? DataResult.error(() -> "Crafting time below 30 ticks is unsupported")
                                        : DataResult.success(craftingTime),
                                DataResult::success
                        )
                        .forGetter(recipe -> recipe.craftingTime),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf(CommonKeys.WORKTABLE).forGetter(recipe -> recipe.worktableInput),
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf(CommonKeys.PEDESTALS)
                        .flatXmap(
                                ingredients -> {
                                    var nonEmptyIngredients = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                    if (nonEmptyIngredients.length == 0) {
                                        return DataResult.error(() -> "No pedestal ingredients for a Lensed worktable recipe");
                                    } else {
                                        return nonEmptyIngredients.length > 4
                                                ? DataResult.error(() -> "Too many pedestal ingredients for a Lensed worktable recipe")
                                                : DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, nonEmptyIngredients));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(recipe -> recipe.pedestalInputs)
        ).apply(instance, LensedWorktableRecipe::new));
        public static final PacketCodec<RegistryByteBuf, LensedWorktableRecipe> PACKET_CODEC = PacketCodec.tuple(
                ItemStack.PACKET_CODEC, recipe -> recipe.result,
                PacketCodecs.INTEGER, recipe -> recipe.craftingTime,
                Ingredient.PACKET_CODEC, recipe -> recipe.worktableInput,
                PacketCodecs.collection(DefaultedList::ofSize, Ingredient.PACKET_CODEC), recipe -> recipe.pedestalInputs,
                LensedWorktableRecipe::new
        );

        @Override
        public MapCodec<LensedWorktableRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, LensedWorktableRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
