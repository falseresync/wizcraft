package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.data.component.WizcraftDataComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WandItem extends Item {
    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            var stackInSlot = slot.getStack().copy();
            var previousFocusStack = stack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);

            if (slot.canInsert(previousFocusStack)) {
                if (!previousFocusStack.isEmpty() && stackInSlot.isEmpty()) {
                    slot.setStack(previousFocusStack.copy());
                    stack.remove(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM);
                    return true;
                } else if (stackInSlot.getItem() instanceof FocusItem && slot.canTakeItems(player)) {
                    slot.setStack(previousFocusStack.copy());
                    stack.set(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, stackInSlot);
                    return true;
                }
            }
        }
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var wandStack = user.getStackInHand(hand);
        var focusStack = wandStack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUse(wandStack, focusStack, world, user, hand);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var wandStack = context.getStack();
        var focusStack = wandStack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnBlock(wandStack, focusStack, context);
        }
        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        var wandStack = user.getStackInHand(hand);
        var focusStack = wandStack.getOrDefault(WizcraftDataComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnEntity(wandStack, focusStack, user, entity, hand);
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
