package falseresync.wizcraft.common.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ContainerComponent(ImmutableList<ItemStack> stacks, int size) implements TooltipComponent {
    public static final Codec<ContainerComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Slot.CODEC.listOf().xmap(ContainerComponent::fromSlots, ContainerComponent::toSlots)
                    .fieldOf("slots").forGetter(ContainerComponent::stacks),
            Codec.INT.fieldOf("size").forGetter(ContainerComponent::size)
    ).apply(instance, ContainerComponent::createRespectingSize));
    public static final StreamCodec<RegistryFriendlyByteBuf, ContainerComponent> PACKET_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_LIST_STREAM_CODEC, ContainerComponent::stacks,
            ByteBufCodecs.INT, ContainerComponent::size,
            ContainerComponent::new
    );

    public ContainerComponent(List<ItemStack> stacks, int size) {
        this(ImmutableList.copyOf(stacks), size);
    }

    private static ContainerComponent createRespectingSize(List<ItemStack> stacks, int size) {
        if (size > stacks.size()) {
            var builder = ImmutableList.<ItemStack>builder();
            builder.addAll(stacks);
            for (int i = stacks.size(); i < size; i++) {
                builder.add(ItemStack.EMPTY);
            }

            return new ContainerComponent(builder.build(), size);
        }

        return new ContainerComponent(stacks, size);
    }

    public static ContainerComponent createDefault(int size) {
        return new ContainerComponent(NonNullList.withSize(size, ItemStack.EMPTY), size);
    }

    public static List<ItemStack> fromSlots(List<Slot> slots) {
        var stacks = NonNullList.withSize(slots.stream().mapToInt(Slot::index).max().orElseThrow() + 1, ItemStack.EMPTY);
        for (Slot slot : slots) {
            stacks.set(slot.index, slot.stack);
        }
        return stacks;
    }

    public static List<Slot> toSlots(List<ItemStack> stacks) {
        var builder = ImmutableList.<Slot>builder();
        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i);
            if (!stack.isEmpty()) {
                builder.add(new Slot(i, stack));
            }
        }
        return builder.build();
    }

    public int getSlotWithStack(ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (!stacks.get(i).isEmpty() && ItemStack.isSameItemSameComponents(stack, stacks.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public EphemeralInventory toModifiable() {
        return new EphemeralInventory(this);
    }

    @Override
    public String toString() {
        return "InventoryComponent" + stacks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ContainerComponent that) {
            return this.stacks.size() == that.stacks.size() && ItemStack.listMatches(this.stacks, that.stacks);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(stacks);
    }

    public record Slot(int index, ItemStack stack) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 255).fieldOf("index").forGetter(Slot::index),
                ItemStack.CODEC.fieldOf("stack").forGetter(Slot::stack)
        ).apply(instance, Slot::new));
    }
}
