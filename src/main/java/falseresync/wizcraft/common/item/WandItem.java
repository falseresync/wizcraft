package falseresync.wizcraft.common.item;

import falseresync.wizcraft.client.*;
import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.data.component.*;
import falseresync.wizcraft.common.item.focus.*;
import net.fabricmc.fabric.api.client.keybinding.v1.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.*;
import net.minecraft.screen.slot.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class WandItem extends Item implements ActivatorItem {
    public WandItem(Settings settings) {
        super(settings
                .component(WizcraftComponents.WAND_CHARGE, 0)
                .component(WizcraftComponents.WAND_MAX_CHARGE, 100));
    }

    // Wand as an item

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean onStackClicked(ItemStack wandStack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.RIGHT) {
            var exchange = exchangeFocuses(wandStack, slot.getStack().copy(), player);
            if (exchange.getResult().isAccepted()) {
                slot.setStack(exchange.getValue());
                return true;
            }
        }
        return super.onStackClicked(wandStack, slot, clickType, player);
    }

    @Override
    public void postProcessComponents(ItemStack stack) {
        var charge = stack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
        var maxCharge = stack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
        if (charge > maxCharge) {
            stack.set(WizcraftComponents.WAND_CHARGE, maxCharge);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    // Focus actions processing

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var wandStack = user.getStackInHand(hand);
        var focusStack = getEquipped(wandStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUse(wandStack, focusStack, world, user, hand);
        }

        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var activationResult = activateBlock(WAND_BEHAVIORS, context);
        if (activationResult.isAccepted()) return activationResult;

        var wandStack = context.getStack();
        var focusStack = getEquipped(wandStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnBlock(wandStack, focusStack, context);
        }

        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        var wandStack = user.getStackInHand(hand);
        var focusStack = getEquipped(wandStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnEntity(wandStack, focusStack, user, entity, hand);
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusUsageTick(world, user, stack, focusStack, remainingUseTicks);
            return;
        }

        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusFinishUsing(stack, focusStack, world, user);
        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusOnStoppedUsing(stack, focusStack, world, user, remainingUseTicks);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusInventoryTick(stack, focusStack, world, entity, slot, selected);
        }
        if (entity instanceof ServerPlayerEntity player) {
            Wizcraft.getChargeManager().tryChargeWandPassively(stack, world, player);
        }
    }


    // Focus properties processing

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusIsUsedOnRelease(stack, focusStack);
        }

        return super.isUsedOnRelease(stack);
    }

//    @Override
//    public UseAction getUseAction(ItemStack focusStack) {
//        return super.getUseAction(focusStack);
//    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetMaxUseTime(stack, focusStack, user);
        }

        return super.getMaxUseTime(stack, user);
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        var weaponStack = damageSource.getWeaponStack();
        if (weaponStack != null && weaponStack.getItem() instanceof WandItem) {
            var focusStack = getEquipped(weaponStack);
            if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
                return focusItem.focusGetBonusAttackDamage(weaponStack, focusStack, target, baseAttackDamage, damageSource);
            }
        }

        return super.getBonusAttackDamage(target, baseAttackDamage, damageSource);
    }

    // Focus appearance processing

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusIsItemBarVisible(stack, focusStack);
        }

        return super.isItemBarVisible(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetItemBarStep(stack, focusStack);
        }

        return super.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetItemBarColor(stack, focusStack);
        }

        return super.getItemBarColor(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusHasGlint(stack, focusStack);
        }

        return super.hasGlint(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            tooltip.add(Text
                    .translatable("tooltip.wizcraft.wand.active_focus", focusStack.getName())
                    .styled(style -> style.withColor(Formatting.GRAY)));
            focusItem.focusAppendTooltip(stack, focusStack, context, tooltip, type);
        }
        tooltip.add(Text
                .translatable("tooltip.wizcraft.wand.change_focus", KeyBindingHelper.getBoundKeyOf(WizcraftKeybindings.TOOL_CONTROL).getLocalizedText())
                .styled(style -> style.withColor(Formatting.GRAY).withItalic(true)));
        super.appendTooltip(stack, context, tooltip, type);
    }


    // Custom wand methods

    public TypedActionResult<ItemStack> exchangeFocuses(ItemStack wandStack, ItemStack newFocusStack, PlayerEntity user) {
        var oldFocusStack = getEquipped(wandStack);

        var removeOld = false;
        var insertNew = false;

        // newFocus = empty, oldFocus = empty -> pass newFocus
        if (newFocusStack.isEmpty() && oldFocusStack.isEmpty()) {
            return TypedActionResult.pass(newFocusStack);
        }

        if (oldFocusStack.getItem() instanceof FocusItem oldFocusItem) {
            removeOld = true;
            oldFocusItem.focusOnUnequipped(wandStack, oldFocusStack, user);
        }

        if (newFocusStack.getItem() instanceof FocusItem newFocusItem) {
            insertNew = true;
            newFocusItem.focusOnEquipped(wandStack, newFocusStack, user);
        }

        // newFocus != empty, oldFocus == empty -> success oldFocus
        // newFocus != empty, oldFocus != empty -> success oldFocus
        if (insertNew) {
            wandStack.set(WizcraftComponents.EQUIPPED_FOCUS_ITEM, newFocusStack);
            return TypedActionResult.success(oldFocusStack);
        }

        // newFocus = empty, oldFocus != empty -> success oldFocus
        if (removeOld) {
            wandStack.remove(WizcraftComponents.EQUIPPED_FOCUS_ITEM);
            return TypedActionResult.success(oldFocusStack);
        }

        // newFocus is not empty, but not a focus item -> fail newFocus
        return TypedActionResult.fail(newFocusStack);
    }

    public ItemStack getEquipped(ItemStack wandStack) {
        return wandStack.getOrDefault(WizcraftComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
    }
}
