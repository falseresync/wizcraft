package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

import static falseresync.wizcraft.common.Wizcraft.wid;

public record CountableIngredient(Ingredient vanilla, int count) implements CustomIngredient {
    @Override
    public boolean test(ItemStack stack) {
        return vanilla.test(stack) && stack.getCount() == count;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return Arrays.asList(vanilla.getMatchingStacks());
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<CountableIngredient> getSerializer() {
        return WizcraftRecipeCustomIngredients.COUNTABLE_INGREDIENT_SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<CountableIngredient> {
        public static final Identifier ID = wid("countable_ingredient");
        public static final MapCodec<CountableIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("vanilla").forGetter(CountableIngredient::vanilla),
                Codec.INT.optionalFieldOf("count", 1).validate(amount -> {
                    if (amount < 1) {
                        return DataResult.error(() -> "A count of ingredient must not be lower than 1");
                    } else if (amount > 10) {
                        return DataResult.error(() -> "A count of ingredient must not be greater than 10");
                    } else {
                        return DataResult.success(amount);
                    }
                }).forGetter(CountableIngredient::count)
        ).apply(instance, CountableIngredient::new));
        public static final PacketCodec<RegistryByteBuf, CountableIngredient> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, CountableIngredient::vanilla,
                PacketCodecs.INTEGER, CountableIngredient::count,
                CountableIngredient::new
        );

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<CountableIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CountableIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}
