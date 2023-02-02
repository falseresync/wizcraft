package ru.falseresync.wizcraft.common.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.world.World;

public interface BaseRecipe extends Recipe<Inventory> {
    @Override
    default boolean matches(Inventory inventory, World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack craft(Inventory inventory) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean fits(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }
}
