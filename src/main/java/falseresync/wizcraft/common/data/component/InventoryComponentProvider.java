package falseresync.wizcraft.common.data.component;

import net.minecraft.item.ItemStack;

public interface InventoryComponentProvider {
    int getInventorySize();
    default InventoryComponent getOrCreateInventoryComponent(ItemStack stack) {
        return stack.getOrDefault(WizcraftDataComponents.INVENTORY, InventoryComponent.createDefault(getInventorySize()));
    }
}
