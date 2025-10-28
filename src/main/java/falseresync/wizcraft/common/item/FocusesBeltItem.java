package falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.*;
import falseresync.wizcraft.common.data.InventoryComponentProvider;
import falseresync.wizcraft.common.data.WizcraftComponents;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.*;
import net.minecraft.screen.slot.*;
import net.minecraft.util.*;

import java.util.*;

public class FocusesBeltItem extends TrinketItem implements InventoryComponentProvider {
    public static final int INVENTORY_SIZE = 12;

    public FocusesBeltItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            var remainder = exchangeStack(stack, slot.getStack());
            if (remainder.getResult().isAccepted()) {
                slot.setStack(remainder.getValue());
                return true;
            }
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    public Optional<ItemStack> findTrinketStack(PlayerEntity player) {
        return TrinketsApi.getTrinketComponent(player)
                .map(trinketComponent -> trinketComponent.getEquipped(WizcraftItems.FOCUSES_BELT))
                .flatMap(equipped -> equipped.isEmpty() ? Optional.empty() : Optional.of(equipped.getFirst().getRight()));
    }

    public TypedActionResult<ItemStack> exchangeStack(ItemStack beltStack, ItemStack stackInSlot) {
        var inventory = getOrCreateInventoryComponent(beltStack).toModifiable();
        var remainder = ItemStack.EMPTY;
        var dirty = false;
        if (stackInSlot.isIn(WizcraftItemTags.FOCUSES)) {
            remainder = inventory.addStack(stackInSlot);
            dirty = true;
        } else if (stackInSlot.isEmpty()) {
            for (int i = 0; i < inventory.size(); i++) {
                if (!inventory.getStack(i).isEmpty()) {
                    remainder = inventory.removeStack(i);
                    dirty = true;
                    break;
                }
            }
        }
        if (dirty) {
            inventory.flush(beltStack);
            return TypedActionResult.success(remainder);
        }
        return TypedActionResult.fail(remainder);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(WizcraftComponents.INVENTORY));
    }

    @Override
    public int getDefaultInventorySize() {
        return INVENTORY_SIZE;
    }
}
