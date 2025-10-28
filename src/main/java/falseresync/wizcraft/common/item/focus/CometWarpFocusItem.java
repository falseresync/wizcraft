package falseresync.wizcraft.common.item.focus;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.WizcraftSounds;
import falseresync.wizcraft.common.data.WizcraftComponents;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;

public class CometWarpFocusItem extends FocusItem {
    public static final BlockPredicatesChecker LODESTONE_CHECKER = new BlockPredicatesChecker(List.of(
            BlockPredicate.Builder.create()
                    .blocks(Blocks.LODESTONE)
                    .build()
    ), false);
    public static final int DEFAULT_PLACEMENT_COST = 5;
    public static final int DEFAULT_WARPING_COST = 15;
    public static final int DEFAULT_INTERDIMENSIONAL_COST = 30;
    public static final int PERSISTENT_ANCHOR_COST_COEFFICIENT = 2;

    public CometWarpFocusItem(Settings settings) {
        super(settings.component(DataComponentTypes.CAN_PLACE_ON, LODESTONE_CHECKER));
    }

    private static void reportPermanentlyBound(PlayerEntity player, GlobalPos permanentAnchor) {
        player.sendMessage(Text.translatable("hud.wizcraft.focus.comet_warp.permanently_anchored", permanentAnchor.dimension().getValue().getPath(), permanentAnchor.pos().toShortString()), true);
    }

    private static void appendTooltip$internal(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type, boolean showSetupTip) {
        if (focusStack.contains(WizcraftComponents.TOOLTIP_OVERRIDDEN)) {
            return;
        }

        var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
        if (anchor != null) {
            tooltip.add(Text.translatable(
                            "tooltip.wizcraft.wand.has_anchor",
                            anchor.dimension().getValue().getPath(),
                            anchor.pos().toShortString())
                    .formatted(Formatting.GRAY));
        }

        var persistentAnchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        if (persistentAnchor != null) {
            var key = focusStack.contains(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)
                    ? "tooltip.wizcraft.wand.has_anchor"
                    : "tooltip.wizcraft.wand.has_permanent_anchor";
            tooltip.add(Text.translatable(
                            key,
                            persistentAnchor.dimension().getValue().getPath(),
                            persistentAnchor.pos().toShortString())
                    .formatted(Formatting.GRAY));
        }

        if (anchor == null && persistentAnchor == null && showSetupTip) {
            tooltip.add(Text.translatable("tooltip.wizcraft.wand.setup_anchor").formatted(Formatting.GRAY));
        }
    }

    @Override
    public void focusOnEquipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        transferComponent(focusStack, wandStack, WizcraftComponents.WARP_FOCUS_ANCHOR);
        transferComponent(focusStack, wandStack, WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public void focusOnUnequipped(ItemStack wandStack, ItemStack focusStack, PlayerEntity user) {
        transferComponent(wandStack, focusStack, WizcraftComponents.WARP_FOCUS_ANCHOR);
        transferComponent(wandStack, focusStack, WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public ActionResult focusUseOnBlock(ItemStack wandStack, ItemStack focusStack, ItemUsageContext context) {
        var world = context.getWorld();
        var player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }

        // Do not override lodestones
        var persistentAnchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        if (persistentAnchor != null && !focusStack.contains(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
            if (world.isClient && player.isSneaking()) {
                reportPermanentlyBound(player, persistentAnchor);
            }
            return ActionResult.PASS;
        }

        var pos = context.getBlockPos();
        if (focusStack.canPlaceOn(new CachedBlockPosition(world, pos, false))) {
            if (!world.isClient && player.isSneaking()) {
                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, DEFAULT_PLACEMENT_COST * PERSISTENT_ANCHOR_COST_COEFFICIENT, player)) {
                    Reports.insufficientCharge(player);
                    return ActionResult.FAIL;
                }

                player.playSoundToPlayer(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1f, 1f);
                wandStack.set(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR, GlobalPos.create(world.getRegistryKey(), pos.up()));
                wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                focusStack.damage(1, player, EquipmentSlot.MAINHAND);
                return ActionResult.SUCCESS;
            }

            return ActionResult.CONSUME;
        } else if (focusStack.contains(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
            if (world.isClient) {
                player.sendMessage(Text.translatable("hud.wizcraft.focus.comet_warp.cannot_anchor_here"), true);
            }
            return ActionResult.FAIL;
        }

        return super.focusUseOnBlock(wandStack, focusStack, context);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld serverWorld) {
            if (user.isSneaking()) {
                // Do not override lodestones
                var persistentAnchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                if (persistentAnchor != null) {
                    if (!focusStack.contains(WizcraftComponents.WARP_FOCUS_BLOCK_ONLY_MODE)) {
                        reportPermanentlyBound(player, persistentAnchor);
                    }
                    return TypedActionResult.fail(wandStack);
                }

                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, DEFAULT_PLACEMENT_COST, user)) {
                    Reports.insufficientCharge(player);
                    return TypedActionResult.fail(wandStack);
                }

                player.playSound(WizcraftSounds.COMET_WARP_ANCHOR_PLACED);
                wandStack.set(WizcraftComponents.WARP_FOCUS_ANCHOR, GlobalPos.create(world.getRegistryKey(), user.getBlockPos()));
                if (world.random.nextFloat() < 0.1f) {
                    focusStack.damage(1, player, EquipmentSlot.MAINHAND);
                }
            } else {
                var anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_ANCHOR);
                var persistent = false;
                if (anchor == null) {
                    anchor = wandStack.get(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                    persistent = true;
                }

                if (anchor == null) {
                    player.playSoundToPlayer(SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 1f, 1f);
                    player.sendMessage(Text.translatable("hud.wizcraft.focus.comet_warp.no_anchor"), true);
                    return TypedActionResult.fail(wandStack);
                }

                var destination = serverWorld.getServer().getWorld(anchor.dimension());
                if (destination == null) {
                    // Broken anchor
                    player.playSoundToPlayer(SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                    wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                    wandStack.remove(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
                    return TypedActionResult.fail(wandStack);
                }

                int warpingCost = destination.getDimension() != world.getDimension() ? DEFAULT_INTERDIMENSIONAL_COST : DEFAULT_WARPING_COST;
                if (persistent) {
                    warpingCost /= PERSISTENT_ANCHOR_COST_COEFFICIENT;
                }

                if (!Wizcraft.getChargeManager().tryExpendWandCharge(wandStack, warpingCost, user)) {
                    Reports.insufficientCharge(player);
                    return TypedActionResult.fail(wandStack);
                }

                player.playSound(SoundEvents.ENTITY_PLAYER_TELEPORT);
                user.teleportTo(new TeleportTarget(destination, anchor.pos().toCenterPos(), Vec3d.ZERO, user.getYaw(), user.getPitch(), TeleportTarget.NO_OP));
                if (!persistent) {
                    wandStack.remove(WizcraftComponents.WARP_FOCUS_ANCHOR);
                }
                focusStack.damage(1, player, EquipmentSlot.MAINHAND);
            }
            return TypedActionResult.success(wandStack);
        }

        return TypedActionResult.consume(wandStack);
    }

    @Override
    public boolean focusHasGlint(ItemStack wandStack, ItemStack focusStack) {
        return hasGlint(wandStack);
    }

    @Override
    public void focusAppendTooltip(ItemStack wandStack, ItemStack focusStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        appendTooltip$internal(wandStack, focusStack, context, tooltip, type, true);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(WizcraftComponents.WARP_FOCUS_ANCHOR) || stack.contains(WizcraftComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        appendTooltip$internal(stack, stack, context, tooltip, type, false);
    }
}
