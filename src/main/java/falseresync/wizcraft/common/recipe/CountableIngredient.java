package falseresync.wizcraft.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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
        return Arrays.asList(vanilla.getItems());
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<CountableIngredient> getSerializer() {
        return WizcraftCustomIngredients.COUNTABLE_INGREDIENT_SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<CountableIngredient> {
        public static final ResourceLocation ID = wid("countable_ingredient");
        public static final MapCodec<CountableIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("vanilla").forGetter(CountableIngredient::vanilla),
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
        public static final StreamCodec<RegistryFriendlyByteBuf, CountableIngredient> PACKET_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CountableIngredient::vanilla,
                ByteBufCodecs.INT, CountableIngredient::count,
                CountableIngredient::new
        );

        @Override
        public ResourceLocation getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<CountableIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CountableIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}
