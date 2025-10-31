package falseresync.wizcraft.common.item;

import falseresync.wizcraft.client.WizcraftKeybindings;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.data.WizcraftComponents;
import falseresync.wizcraft.common.item.focus.FocusItem;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class WandItem extends Item implements ActivatorItem {
    public WandItem(Properties settings) {
        super(settings
                .component(WizcraftComponents.WAND_CHARGE, 0)
                .component(WizcraftComponents.WAND_MAX_CHARGE, 100));
    }

    // Wand as an item

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack wandStack, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.SECONDARY) {
            var exchange = exchangeFocuses(wandStack, slot.getItem().copy(), player);
            if (exchange.getResult().consumesAction()) {
                slot.setByPlayer(exchange.getObject());
                return true;
            }
        }
        return super.overrideStackedOnOther(wandStack, slot, clickType, player);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        var charge = stack.getOrDefault(WizcraftComponents.WAND_CHARGE, 0);
        var maxCharge = stack.getOrDefault(WizcraftComponents.WAND_MAX_CHARGE, 0);
        if (charge > maxCharge) {
            stack.set(WizcraftComponents.WAND_CHARGE, maxCharge);
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    // Focus actions processing

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        var wandStack = user.getItemInHand(hand);
        var focusStack = getEquipped(wandStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUse(wandStack, focusStack, world, user, hand);
        }

        return super.use(world, user, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var activationResult = activateBlock(WAND_BEHAVIORS, context);
        if (activationResult.consumesAction()) return activationResult;

        var wandStack = context.getItemInHand();
        var focusStack = getEquipped(wandStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOn(wandStack, focusStack, context);
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        var wandStack = user.getItemInHand(hand);
        var focusStack = getEquipped(wandStack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusInteractLivingEntity(wandStack, focusStack, user, entity, hand);
        }

        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusOnUseTick(level, user, stack, focusStack, remainingUseTicks);
            return;
        }

        super.onUseTick(level, user, stack, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusFinishUsingItem(stack, focusStack, level, user);
        }

        return super.finishUsingItem(stack, level, user);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusReleaseUsing(stack, focusStack, level, user, remainingUseTicks);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            focusItem.focusInventoryTick(stack, focusStack, level, entity, slot, selected);
        }
        if (entity instanceof ServerPlayer player) {
            Wizcraft.getChargeManager().tryChargeWandPassively(stack, level, player);
        }
    }


    // Focus properties processing

    @Override
    public boolean useOnRelease(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusUseOnRelease(stack, focusStack);
        }

        return super.useOnRelease(stack);
    }

//    @Override
//    public UseAction getUseAction(ItemStack focusStack) {
//        return super.getUseAction(focusStack);
//    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetUseDuration(stack, focusStack, user);
        }

        return super.getUseDuration(stack, user);
    }

    @Override
    public float getAttackDamageBonus(Entity target, float baseAttackDamage, DamageSource damageSource) {
        var weaponStack = damageSource.getWeaponItem();
        if (weaponStack != null && weaponStack.getItem() instanceof WandItem) {
            var focusStack = getEquipped(weaponStack);
            if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
                return focusItem.focusGetAttackDamageBonus(weaponStack, focusStack, target, baseAttackDamage, damageSource);
            }
        }

        return super.getAttackDamageBonus(target, baseAttackDamage, damageSource);
    }

    // Focus appearance processing

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusIsBarVisible(stack, focusStack);
        }

        return super.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetBarWidth(stack, focusStack);
        }

        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusGetBarColor(stack, focusStack);
        }

        return super.getBarColor(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            return focusItem.focusIsFoil(stack, focusStack);
        }

        return super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        var focusStack = getEquipped(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof FocusItem focusItem) {
            tooltip.add(Component
                    .translatable("tooltip.wizcraft.wand.active_focus", focusStack.getHoverName())
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY)));
            focusItem.focusAppendHoverText(stack, focusStack, context, tooltip, type);
        }
        tooltip.add(Component
                .translatable("tooltip.wizcraft.wand.change_focus", KeyBindingHelper.getBoundKeyOf(WizcraftKeybindings.TOOL_CONTROL).getDisplayName())
                .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)));
        super.appendHoverText(stack, context, tooltip, type);
    }


    // Custom wand methods

    public InteractionResultHolder<ItemStack> exchangeFocuses(ItemStack wandStack, ItemStack newFocusStack, Player user) {
        var oldFocusStack = getEquipped(wandStack);

        var removeOld = false;
        var insertNew = false;

        // newFocus = empty, oldFocus = empty -> pass newFocus
        if (newFocusStack.isEmpty() && oldFocusStack.isEmpty()) {
            return InteractionResultHolder.pass(newFocusStack);
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
            return InteractionResultHolder.success(oldFocusStack);
        }

        // newFocus = empty, oldFocus != empty -> success oldFocus
        if (removeOld) {
            wandStack.remove(WizcraftComponents.EQUIPPED_FOCUS_ITEM);
            return InteractionResultHolder.success(oldFocusStack);
        }

        // newFocus is not empty, but not a focus item -> fail newFocus
        return InteractionResultHolder.fail(newFocusStack);
    }

    public ItemStack getEquipped(ItemStack wandStack) {
        return wandStack.getOrDefault(WizcraftComponents.EQUIPPED_FOCUS_ITEM, ItemStack.EMPTY);
    }
}
