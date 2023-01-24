package ru.falseresync.wizcraft.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CatalyticCondensationRecipe implements Recipe<Inventory> {
    @Override
    public boolean matches(Inventory inventory, World world) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return null;
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
