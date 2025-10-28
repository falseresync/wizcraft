package falseresync.wizcraft.common.data;

import net.minecraft.inventory.*;
import net.minecraft.item.*;

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
        stack.set(WizcraftComponents.INVENTORY, toImmutable());
    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        throw new UnsupportedOperationException();
    }
}
