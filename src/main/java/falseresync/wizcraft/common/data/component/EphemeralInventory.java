package falseresync.wizcraft.common.data.component;

import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class EphemeralInventory extends SimpleInventory {
    private final InventoryComponent backingComponent;

    public EphemeralInventory(InventoryComponent backingComponent) {
        super(backingComponent.stacks().toArray(new ItemStack[backingComponent.size()]));
        this.backingComponent = backingComponent;
    }

    public InventoryComponent toImmutable() {
        return new InventoryComponent(heldStacks, backingComponent.size());
    }

    public void flush(ItemStack stack) {
        stack.set(WizcraftDataComponents.INVENTORY, toImmutable());
    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        throw new UnsupportedOperationException();
    }
}
