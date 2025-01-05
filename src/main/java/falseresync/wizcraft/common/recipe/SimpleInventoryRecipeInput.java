package falseresync.wizcraft.common.recipe;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class SimpleInventoryRecipeInput extends SimpleInventory implements RecipeInput {
    public SimpleInventoryRecipeInput(int size) {
        super(size);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getStack(slot);
    }

    @Override
    public int getSize() {
        return size();
    }
}
