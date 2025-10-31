package falseresync.wizcraft.common.data;

import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class EphemeralInventory extends SimpleContainer {
    private final ContainerComponent backingComponent;

    public EphemeralInventory(ContainerComponent backingComponent) {
        super(backingComponent.stacks().toArray(new ItemStack[backingComponent.size()]));
        this.backingComponent = backingComponent;
    }

    public ContainerComponent toImmutable() {
        return new ContainerComponent(items, backingComponent.size());
    }

    public void flush(ItemStack stack) {
        stack.set(WizcraftComponents.INVENTORY, toImmutable());
    }

    @Override
    public void addListener(ContainerListener listener) {
        throw new UnsupportedOperationException();
    }
}
