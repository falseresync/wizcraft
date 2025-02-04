package falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import falseresync.wizcraft.common.data.component.FocusesBeltComponent;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.TypedActionResult;

import java.util.Optional;

public class FocusesBeltItem extends TrinketItem {
    public FocusesBeltItem(Settings settings) {
        super(settings.component(WizcraftDataComponents.FOCUSES_BELT, FocusesBeltComponent.DEFAULT));
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

    public Optional<FocusesBeltComponent> findTrinketContents(PlayerEntity player) {
        return findTrinketStack(player).map(stack -> {
            var contents = getContents(stack);
            contents.addListener(changed -> stack.set(WizcraftDataComponents.FOCUSES_BELT, (FocusesBeltComponent) changed));
            return contents;
        });
    }

    public TypedActionResult<ItemStack> exchangeStack(ItemStack beltStack, ItemStack stackInSlot) {
        var contents = getContents(beltStack);
        var remainder = ItemStack.EMPTY;
        var dirty = false;
        if (stackInSlot.isIn(WizcraftItemTags.FOCUSES)) {
            remainder = contents.addStack(stackInSlot);
            dirty = true;
        } else if (stackInSlot.isEmpty()) {
            for (int i = 0; i < contents.size(); i++) {
                if (!contents.getStack(i).isEmpty()) {
                    remainder = contents.removeStack(i);
                    dirty = true;
                    break;
                }
            }
        }
        if (dirty) {
            beltStack.set(WizcraftDataComponents.FOCUSES_BELT, contents);
            return TypedActionResult.success(remainder);
        }
        return TypedActionResult.fail(remainder);
    }

    public FocusesBeltComponent getContents(ItemStack beltStack) {
        return beltStack.getOrDefault(WizcraftDataComponents.FOCUSES_BELT, FocusesBeltComponent.DEFAULT);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(WizcraftDataComponents.FOCUSES_BELT));
    }
}
