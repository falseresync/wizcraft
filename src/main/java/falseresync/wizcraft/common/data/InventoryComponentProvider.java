package falseresync.wizcraft.common.data;

import net.minecraft.item.*;

public interface InventoryComponentProvider {
    int getDefaultInventorySize();

    default int getInventorySize(ItemStack stack) {
        return stack.getOrDefault(WizcraftComponents.INVENTORY_SIZE, getDefaultInventorySize());
    }

    default InventoryComponent getOrCreateInventoryComponent(ItemStack stack) {
        return stack.getOrDefault(WizcraftComponents.INVENTORY, InventoryComponent.createDefault(getInventorySize(stack)));
    }
}
