package falseresync.wizcraft.common.data.component;


import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

public final class FocusesBeltComponent extends SimpleInventory implements TooltipData {
    public static final FocusesBeltComponent DEFAULT = new FocusesBeltComponent(List.of());
    public static final Codec<FocusesBeltComponent> CODEC = ItemStack.CODEC.listOf().xmap(FocusesBeltComponent::new, FocusesBeltComponent::getHeldStacks);
    public static final PacketCodec<RegistryByteBuf, FocusesBeltComponent> PACKET_CODEC = ItemStack.LIST_PACKET_CODEC.xmap(FocusesBeltComponent::new, FocusesBeltComponent::getHeldStacks);

    public FocusesBeltComponent(List<ItemStack> stacks) {
        super(12);
        Preconditions.checkArgument(stacks.size() <= 12, "Focuses belt can only store up to 12 focuses");
        Preconditions.checkArgument(stacks.stream().filter(it -> !it.isEmpty()).allMatch(it -> it.isIn(WizcraftItemTags.FOCUSES)), "Focuses belt can only store focuses");
        for (ItemStack stack : stacks) {
            // It's unimplemented for defaulted list??
            //noinspection UseBulkOperation
            this.heldStacks.add(stack);
        }
    }

    public int getSlotWithStack(ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (!heldStacks.get(i).isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, heldStacks.get(i))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public String toString() {
        return "Focuses belt contents" + heldStacks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FocusesBeltComponent) obj;
        return this.heldStacks.size() == that.heldStacks.size() && ItemStack.stacksEqual(this.heldStacks, that.heldStacks);
    }

    @Override
    public int hashCode() {
        return ItemStack.listHashCode(heldStacks);
    }

}
