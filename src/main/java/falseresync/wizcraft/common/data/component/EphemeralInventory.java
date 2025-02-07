package falseresync.wizcraft.common.data.component;

import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class EphemeralInventory extends SimpleInventory {
    public EphemeralInventory(InventoryComponent backingComponent) {
        super(backingComponent.stacks().toArray(new ItemStack[backingComponent.size()]));
    }

    public InventoryComponent toImmutable() {
        return new InventoryComponent(heldStacks);
    }

    public void flush(ItemStack stack) {
        stack.set(WizcraftDataComponents.INVENTORY, toImmutable());
    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        throw new UnsupportedOperationException();
    }
}
