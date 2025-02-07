package falseresync.wizcraft.common.data.component;

import net.minecraft.item.ItemStack;

public interface InventoryComponentProvider {
    int getDefaultInventorySize();

    default int getInventorySize(ItemStack stack) {
        return stack.getOrDefault(WizcraftDataComponents.INVENTORY_SIZE, getDefaultInventorySize());
    }

    default InventoryComponent getOrCreateInventoryComponent(ItemStack stack) {
        return stack.getOrDefault(WizcraftDataComponents.INVENTORY, InventoryComponent.createDefault(getInventorySize(stack)));
    }
}
