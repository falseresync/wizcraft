package falseresync.wizcraft.common.recipe;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.input.*;

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
