package falseresync.wizcraft.common.data;

import net.minecraft.world.item.ItemStack;

public interface InventoryComponentProvider {
    int getDefaultInventorySize();

    default int getInventorySize(ItemStack stack) {
        return stack.getOrDefault(WizcraftComponents.INVENTORY_SIZE, getDefaultInventorySize());
    }

    default ContainerComponent getOrCreateInventoryComponent(ItemStack stack) {
        return stack.getOrDefault(WizcraftComponents.INVENTORY, ContainerComponent.createDefault(getInventorySize(stack)));
    }
}
