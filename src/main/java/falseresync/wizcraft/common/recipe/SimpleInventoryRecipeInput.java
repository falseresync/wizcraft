package falseresync.wizcraft.common.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class SimpleInventoryRecipeInput extends SimpleContainer {
    private final RecipeInput recipeInput = new RecipeInput() {
        @Override
        public ItemStack getItem(int index) {
            return SimpleInventoryRecipeInput.this.getItem(index);
        }

        @Override
        public int size() {
            return SimpleInventoryRecipeInput.this.getContainerSize();
        }

        @Override
        public boolean isEmpty() {
            return SimpleInventoryRecipeInput.this.isEmpty();
        }
    };

    public SimpleInventoryRecipeInput(int size) {
        super(size);
    }

    public RecipeInput recipeInput() {
        return recipeInput;
    }
}
