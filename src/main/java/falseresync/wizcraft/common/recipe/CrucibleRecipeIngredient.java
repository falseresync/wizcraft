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

public record CrucibleRecipeIngredient(Ingredient vanilla, int count) implements CustomIngredient {
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
    public CustomIngredientSerializer<CrucibleRecipeIngredient> getSerializer() {
        return WizcraftRecipeCustomIngredients.CRUCIBLE_RECIPE_INGREDIENT_SERIALIZER;
    }

    public static class Serializer implements CustomIngredientSerializer<CrucibleRecipeIngredient> {
        public static final Identifier ID = wid("crucible_recipe_ingredient");
        public static final MapCodec<CrucibleRecipeIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("vanilla").forGetter(CrucibleRecipeIngredient::vanilla),
                Codec.INT.optionalFieldOf("count", 1).validate(amount -> {
                    if (amount < 1) {
                        return DataResult.error(() -> "A count of ingredient must not be lower than 1");
                    } else if (amount > 10) {
                        return DataResult.error(() -> "A count of ingredient must not be greater than 10");
                    } else {
                        return DataResult.success(amount);
                    }
                }).forGetter(CrucibleRecipeIngredient::count)
        ).apply(instance, CrucibleRecipeIngredient::new));
        public static final PacketCodec<RegistryByteBuf, CrucibleRecipeIngredient> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, CrucibleRecipeIngredient::vanilla,
                PacketCodecs.INTEGER, CrucibleRecipeIngredient::count,
                CrucibleRecipeIngredient::new
        );

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<CrucibleRecipeIngredient> getCodec(boolean allowEmpty) {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CrucibleRecipeIngredient> getPacketCodec() {
            return PACKET_CODEC;
        }
    }
}
