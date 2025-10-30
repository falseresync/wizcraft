package falseresync.wizcraft.common.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class SimpleInventoryRecipeInput extends SimpleContainer implements RecipeInput {
    public SimpleInventoryRecipeInput(int size) {
        super(size);
    }

    @Override
    public ItemStack getItem(int slot) {
        return super.getItem(slot);
    }

    @Override
    public int size() {
        return getContainerSize();
    }
}
