package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import falseresync.wizcraft.common.CommonKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static falseresync.wizcraft.common.Wizcraft.wid;

public final class LensedWorktableRecipe implements Recipe<RecipeInput> {
    public static final Identifier TYPE_ID = wid("lensed_worktable");
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
        return WizcraftRecipes.LENSED_WORKTABLE;
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
        public static final PacketCodec<RegistryByteBuf, LensedWorktableRecipe> PACKET_CODEC = PacketCodec.ofStatic(Serializer::write, Serializer::read);

        @Override
        public MapCodec<LensedWorktableRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, LensedWorktableRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static LensedWorktableRecipe read(RegistryByteBuf buf) {
            var result = ItemStack.PACKET_CODEC.decode(buf);
            var craftingTime = buf.readVarInt();
            var worktableInput = Ingredient.PACKET_CODEC.decode(buf);
            var numberOfPedestalInputs = buf.readVarInt();
            var pedestalInputs = DefaultedList.<Ingredient>ofSize(numberOfPedestalInputs);
            for (int i = 0; i < numberOfPedestalInputs; i++) {
                pedestalInputs.add(Ingredient.PACKET_CODEC.decode(buf));
            }
            return new LensedWorktableRecipe(result, craftingTime, worktableInput, pedestalInputs);
        }

        public static void write(RegistryByteBuf buf, LensedWorktableRecipe recipe) {
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
            buf.writeVarInt(recipe.craftingTime);
            Ingredient.PACKET_CODEC.encode(buf, recipe.worktableInput);
            buf.writeVarInt(recipe.pedestalInputs.size());
            for (var pedestalInput : recipe.pedestalInputs) {
                Ingredient.PACKET_CODEC.encode(buf, pedestalInput);
            }
        }
    }
}
