package falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.TrinketItem;
import falseresync.wizcraft.common.data.component.FocusesBeltComponent;
import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

import java.util.Optional;

public class FocusesBeltItem extends TrinketItem {
    public FocusesBeltItem(Settings settings) {
        super(settings.component(WizcraftDataComponents.FOCUSES_BELT, FocusesBeltComponent.DEFAULT));
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            var contents = stack.getOrDefault(WizcraftDataComponents.FOCUSES_BELT, FocusesBeltComponent.DEFAULT);
            var stackInSlot = slot.getStack();
            var dirty = false;
            if (stackInSlot.isIn(WizcraftItemTags.FOCUSES)) {
                var remainder = contents.addStack(stackInSlot);
                slot.setStack(remainder);
                dirty = true;
            } else if (stackInSlot.isEmpty()) {
                for (int i = 0; i < contents.size(); i++) {
                    if (!contents.getStack(i).isEmpty()) {
                        var removed = contents.removeStack(i);
                        slot.setStack(removed);
                        dirty = true;
                        break;
                    }
                }
            }
            if (dirty) {
                stack.set(WizcraftDataComponents.FOCUSES_BELT, contents);
                return true;
            }
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return Optional.ofNullable(stack.get(WizcraftDataComponents.FOCUSES_BELT));
    }
}
