package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.worktable.WorktableBlock;
import dev.falseresync.wizcraft.client.WizKeybindings;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.block.pattern.BetterBlockPattern;
import dev.falseresync.wizcraft.common.report.WizReports;
import dev.falseresync.wizcraft.common.wand.focus.ChargingFocus;
import dev.falseresync.wizcraft.network.s2c.TriggerBlockPatternTipS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WandItem extends Item implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "wand");

    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");
        var stack = user.getStackInHand(hand);
        var wand = Wand.fromStack(stack);
        var result = wand.getFocus().use(world, wand, user);
        return new TypedActionResult<>(result, wand.copyAndAttach(stack));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        if (world.getBlockState(pos).isOf(WizBlocks.DUMMY_WORKTABLE)) {
            if (world.isClient() || context.getPlayer() == null) return ActionResult.CONSUME;

            var testedPatterns = WorktableBlock.getPatternMappings().stream()
                    .map(mapping -> mapping.mapFirst(pattern -> pattern.searchAround(world, pos)))
                    .toList();
            var completedPattern = testedPatterns.stream()
                    .filter(mapping -> mapping.mapFirst(BetterBlockPattern.Match::isCompleted).getFirst())
                    .findFirst();
            if (completedPattern.isPresent()) {
                world.setBlockState(pos, completedPattern.get().getSecond().getDefaultState());
                return ActionResult.SUCCESS;
            }

            var halfwayCompletedPattern = testedPatterns.stream()
                    .filter(mapping -> mapping.mapFirst(BetterBlockPattern.Match::isHalfwayCompleted).getFirst())
                    .findFirst();
            if (halfwayCompletedPattern.isPresent()) {
                var player = (ServerPlayerEntity) context.getPlayer();
                ServerPlayNetworking.send(player, new TriggerBlockPatternTipS2CPacket(
                        halfwayCompletedPattern.get().getFirst().delta().stream().map(CachedBlockPosition::getBlockPos).toList()));
                Report.trigger((ServerPlayerEntity) context.getPlayer(), WizReports.Worktable.INCOMPLETE);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        }

        return super.useOnBlock(context);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        var wand = Wand.fromStack(stack);
        return wand.getFocus().getMaxUsageTicks(wand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        var wand = Wand.fromStack(stack);
        wand.getFocus().tick(world, wand, user, remainingUseTicks);
        wand.attach(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Wizcraft.LOGGER.trace(user.getName() + " interrupted a wand usage");
        var wand = Wand.fromStack(stack);
        wand.getFocus().interrupt(world, wand, user, remainingUseTicks);
        wand.attach(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
        var wand = Wand.fromStack(stack);
        wand.getFocus().finish(world, wand, user);
        return wand.copyAndAttach(stack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        var wand = Wand.fromStack(stack);
        var focus = wand.getFocus();
        return focus instanceof ChargingFocus chargingFocus && chargingFocus.getChargingProgress() != 0
                || super.isItemBarVisible(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ColorHelper.Argb.getArgb(0, 115, 190, 211);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var wand = Wand.fromStack(stack);
        var focus = wand.getFocus();
        if (focus instanceof ChargingFocus chargingFocus) {
            return Math.round(chargingFocus.getChargingProgress() * 13f / focus.getMaxUsageTicks(wand));
        }
        return super.getItemBarStep(stack);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var wand = Wand.fromStack(stack);
        var activeFocus = wand.getFocus();
        tooltip.add(Text.translatable("tooltip.wizcraft.wand.active_focus", activeFocus.getName().getString())
                .styled(style -> style.withColor(Formatting.GRAY)));
        activeFocus.appendTooltip(world, tooltip, context);
        if (world != null && world.isClient()) {
            appendClientTooltip(wand, tooltip, context);
        }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void appendClientTooltip(Wand wand, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(
                "tooltip.wizcraft.wand.change_focus",
                    KeyBindingHelper.getBoundKeyOf(WizKeybindings.TOOL_CONTROL).getLocalizedText().getString()
                ).styled(style -> style.withColor(Formatting.GRAY).withItalic(true)));
    }
}
