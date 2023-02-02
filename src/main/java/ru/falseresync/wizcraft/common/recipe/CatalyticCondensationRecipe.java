package ru.falseresync.wizcraft.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import ru.falseresync.wizcraft.common.init.WizRecipes;

public class CatalyticCondensationRecipe implements BaseRecipe {
    protected final Item catalyst;

    public CatalyticCondensationRecipe() {
        this.catalyst = Items.AIR;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return BaseRecipe.super.getIngredients();
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return WizRecipes.CATALYTIC_CONDENSATION;
    }

    public static class Serializer implements RecipeSerializer<CatalyticCondensationRecipe> {
        public static final Serializer INSTANCE;

        static {
            INSTANCE = new Serializer();
        }

        @Override
        public CatalyticCondensationRecipe read(Identifier id, JsonObject json) {
            return null;
        }

        @Override
        public CatalyticCondensationRecipe read(Identifier id, PacketByteBuf buf) {
            return null;
        }

        @Override
        public void write(PacketByteBuf buf, CatalyticCondensationRecipe recipe) {

        }
    }
}
