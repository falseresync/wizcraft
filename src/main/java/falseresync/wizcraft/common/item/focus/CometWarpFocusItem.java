package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.data.WizcraftComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CometWarpFocusItem extends FocusItem {
    public static final AdventureModePredicate LODESTONE_CHECKER = new AdventureModePredicate(List.of(
            BlockPredicate.Builder.block()
                    .of(Blocks.LODESTONE)
                    .build()
    ), false);
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;
    public static final int PERSISTENT_ANCHOR_COST_COEFFICIENT = 2;

    public CometWarpFocusItem(Properties settings) {
        super(settings.component(DataComponents.CAN_PLACE_ON, LODESTONE_CHECKER));
    }

    private static void reportPermanentlyBound(Player player, GlobalPos permanentAnchor) {
        player.displayClientMessage(Component.translatable("hud.wizcraft.focus.comet_warp.permanently_anchored", permanentAnchor.dimension().location().getPath(), permanentAnchor.pos().toShortString()), true);
    }

    private static void appendTooltip$internal(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Component> tooltip, TooltipFlag type, boolean showSetupTip) {
        if (focusStack.has(WizcraftComponents.TOOLTIP_OVERRIDDEN)) {
            return;
        }

        var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            tooltip.add(Component.translatable(
                            "tooltip.wizcraft.wand.has_anchor",
                            anchor.dimension().location().getPath(),
                            anchor.pos().toShortString())
                    .withStyle(ChatFormatting.GRAY));
        }

        var persistentAnchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        if (persistentAnchor != null) {
            var key = focusStack.has(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)
                    ? "tooltip.wizcraft.wand.has_anchor"
                    : "tooltip.wizcraft.wand.has_permanent_anchor";
            tooltip.add(Component.translatable(
                            key,
                            persistentAnchor.dimension().location().getPath(),
                            persistentAnchor.pos().toShortString())
                    .withStyle(ChatFormatting.GRAY));
        }

        if (anchor == null && persistentAnchor == null && showSetupTip) {
            tooltip.add(Component.translatable("tooltip.wizcraft.wand.setup_anchor").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, Player user) {
        transferComponent(focusStack, wandStack, WizcraftComponents.WARP_FOCUS_ANCHOR);
        transferComponent(focusStack, wandStack, WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, Player user) {
        transferComponent(wandStack, focusStack, WizcraftComponents.WARP_FOCUS_ANCHOR);
        transferComponent(wandStack, focusStack, WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public InteractionResult focusUseOn(ItemStack wandStack, ItemStack focusStack, UseOnContext context) {
        var world = context.getLevel();
        var player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        // Do not override lodestones
        var persistentAnchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        if (persistentAnchor != null && !focusStack.has(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
            if (world.isClientSide && player.isShiftKeyDown()) {
                reportPermanentlyBound(player, persistentAnchor);
            }
            return InteractionResult.PASS;
        }

        var pos = context.getClickedPos();
        if (focusStack.canPlaceOnBlockInAdventureMode(new BlockInWorld(world, pos, false))) {
            if (!world.isClientSide && player.isShiftKeyDown()) {
                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, DEFAULT_PLACEMENT_COST * PERSISTENT_ANCHOR_COST_COEFFICIENT, player)) {
                    Reports.insufficientCharge(player);
                    return InteractionResult.FAIL;
                }

                player.playNotifySound(SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1f, 1f);
                wandStack.set(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR, GlobalPos.of(world.dimension(), pos.above()));
                wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        } else if (focusStack.has(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
            if (world.isClientSide) {
                player.displayClientMessage(Component.translatable("hud.wizcraft.focus.comet_warp.cannot_anchor_here"), true);
            }
            return InteractionResult.FAIL;
        }

        return super.focusUseOn(wandStack, focusStack, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player && world instanceof ServerLevel serverWorld) {
            if (user.isShiftKeyDown()) {
                // Do not override lodestones
                var persistentAnchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                if (persistentAnchor != null) {
                    if (!focusStack.has(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
                        reportPermanentlyBound(player, persistentAnchor);
                    }
                    return InteractionResultHolder.fail(wandStack);
                }

                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, DEFAULT_PLACEMENT_COST, user)) {
                    Reports.insufficientCharge(player);
                    return InteractionResultHolder.fail(wandStack);
                }

                Reports.playSoundToEveryone(player, WizcraftSounds.COMET_WARP_ANCHOR_PLACED);
                wandStack.set(WizcraftComponents.WARP_FOCUS_ANCHOR, GlobalPos.of(world.dimension(), user.blockPosition()));
                if (world.random.nextFloat() < 0.1f) {
                    focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            } else {
                var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
                var persistent = false;
                if (anchor == null) {
                    anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                    persistent = true;
                }

                if (anchor == null) {
                    player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1f, 1f);
                    player.displayClientMessage(Component.translatable("hud.wizcraft.focus.comet_warp.no_anchor"), true);
                    return InteractionResultHolder.fail(wandStack);
                }

                var destination = serverWorld.getServer().getLevel(anchor.dimension());
                if (destination == null) {
                    // Broken anchor
                    player.playNotifySound(SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1f, 1f);
                    wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                    wandStack.remove(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                    return InteractionResultHolder.fail(wandStack);
                }

                int warpingCost = destination.dimensionType() != world.dimensionType() ? DEFAULT_INTERDIMENSIONAL_COST : DEFAULT_WARPING_COST;
                if (persistent) {
                    warpingCost /= PERSISTENT_ANCHOR_COST_COEFFICIENT;
                }

                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, warpingCost, user)) {
                    Reports.insufficientCharge(player);
                    return InteractionResultHolder.fail(wandStack);
                }

                Reports.playSoundToEveryone(player, SoundEvents.PLAYER_TELEPORT);
                user.changeDimension(new DimensionTransition(destination, anchor.pos().getCenter(), Vec3.ZERO, user.getYRot(), user.getXRot(), DimensionTransition.DO_NOTHING));
                if (!persistent) {
                    wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                }
                focusStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }
            return InteractionResultHolder.success(wandStack);
        }

        return InteractionResultHolder.consume(wandStack);
    }

    @Override
    public boolean focusIsFoil(ItemStack wandStack, ItemStack focusStack) {
        return isFoil(wandStack);
    }

    @Override
    public void focusAppendHoverText(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        appendTooltip$internal(wandStack, focusStack, context, tooltip, type, true);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(WizcraftComponents.WARP_FOCUS_ANCHOR) || stack.has(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        appendTooltip$internal(stack, stack, context, tooltip, type, false);
    }
}
