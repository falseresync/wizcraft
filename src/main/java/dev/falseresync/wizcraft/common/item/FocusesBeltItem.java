package dev.falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.TrinketItem;
import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.Wizcraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class FocusesBeltItem extends TrinketItem implements HasId {
    public static final Identifier ID = wid("focuses_belt");

    public FocusesBeltItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) return false;

        var inventory = new FocusesBeltInventory(stack);
        var otherStack = slot.getStack();

        if (otherStack.isEmpty()) {
            inventory.removeFirst().ifPresent(removedStack -> {
                playRemoveOneSound(player);
                var rejected = slot.insertStack(removedStack);
                insertOrDrop(inventory, player, stack, rejected);
            });
        } else {
            if (!(otherStack.getItem() instanceof FocusItem)) return false;

            playInsertSound(player);
            insertOrDrop(inventory, player, stack, otherStack);
        }

        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) return false;

        var inventory = new FocusesBeltInventory(stack);
        if (otherStack.isEmpty()) {
            inventory.removeFirst().ifPresent(removedStack -> {
                playRemoveOneSound(player);
                cursorStackReference.set(removedStack);
            });
        } else {
            if (!(otherStack.getItem() instanceof FocusItem)) return false;

            playInsertSound(player);
            insertOrDrop(inventory, player, stack, otherStack);
        }

        return true;
    }

    protected void insertOrDrop(FocusesBeltInventory inventory, PlayerEntity player, ItemStack stack, ItemStack otherStack) {
        if (inventory.insert(otherStack)) {
            inventory.attachTo(stack);
        } else {
            player.dropStack(otherStack);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }


    protected void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    protected void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    protected void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    public static class FocusesBeltInventory {
        public static final int SIZE = 5;
        protected final Queue<ItemStack> stacks = new ArrayBlockingQueue<>(SIZE);

        protected FocusesBeltInventory(ItemStack parent) {
            read(parent.getOrCreateNbt());
        }

        public Optional<ItemStack> removeFirst() {
            return Optional.ofNullable(stacks.poll());
        }

        public boolean insert(ItemStack stack) {
            if (stack.isEmpty()) return true;

            if (!canInsert(stack)) return false;

            if (stacks.offer(stack.copyWithCount(1))) {
                stack.decrement(1);
                return true;
            }

            return false;
        }

        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof FocusItem && stack.getItem().canBeNested();
        }

        public void attachTo(ItemStack parent) {
            var nbt = parent.getOrCreateNbt();
            write(nbt);
            parent.setNbt(nbt);
        }

        protected void read(NbtCompound nbt) {
            if (!nbt.contains("inventory", NbtElement.LIST_TYPE)) return;

            var list = nbt.getList("inventory", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : list) {
                if (!stacks.offer(ItemStack.fromNbt((NbtCompound) element))) {
                    throw new IllegalStateException("Could not deserialize a Focuses belt inventory");
                }
            }
        }

        protected void write(NbtCompound nbt) {
            var list = new NbtList();
            for (ItemStack stack : stacks) {
                list.add(stack.writeNbt(new NbtCompound()));
            }

            nbt.put("inventory", list);
        }
    }
}
