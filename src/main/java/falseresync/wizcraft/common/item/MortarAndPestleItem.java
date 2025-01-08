package falseresync.wizcraft.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MortarAndPestleItem extends Item {
    public MortarAndPestleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        var damage = stack.getDamage();
        if (damage == 1) {
            return ItemStack.EMPTY;
        } else {
            stack.setDamage(damage - 1);
        }
        return super.getRecipeRemainder(stack);
    }
}
