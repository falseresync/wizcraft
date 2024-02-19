package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.worktable.WorktableVariant;
import dev.falseresync.wizcraft.client.WizcraftKeybindings;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.common.wand.Wand;
import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.common.block.WizcraftBlocks;
import dev.falseresync.wizcraft.common.report.WizcraftReports;
import dev.falseresync.wizcraft.common.wand.focus.ChargingFocus;
import dev.falseresync.wizcraft.network.s2c.TriggerBlockPatternTipS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class WandItem extends Item implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MOD_ID, "wand");

    public WandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Wizcraft.LOGGER.trace(user.getName() + " started using a wand");
        var stack = user.getStackInHand(hand);
        var wand = Wand.fromStack(stack);
        var result = wand.getFocus().use(world, wand, user);
        return new TypedActionResult<>(result, wand.attachToCopyOf(stack));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        if (world.getBlockState(pos).isOf(WizcraftBlocks.DUMMY_WORKTABLE)) {
            if (world.isClient() || context.getPlayer() == null) return ActionResult.CONSUME;

            var player = (ServerPlayerEntity) context.getPlayer();
            var matchedVariants = WorktableVariant
                    .getForPlayerAndSearchAround((ServerWorld) world, player, pos);
            var fullyCompletedVariant = matchedVariants.stream()
                    .filter(variant -> variant.match().isCompleted())
                    .findFirst();
            if (fullyCompletedVariant.isPresent()) {
                world.setBlockState(pos, fullyCompletedVariant.get().block().getDefaultState());
                return ActionResult.SUCCESS;
            }

            var leastUncompletedVariant = matchedVariants.stream()
                    .filter(variant -> variant.match().isHalfwayCompleted())
                    .min(Comparator.comparingInt(variant -> variant.match().delta().size()));
            if (leastUncompletedVariant.isPresent()) {
                ServerPlayNetworking.send(player, new TriggerBlockPatternTipS2CPacket(leastUncompletedVariant.get().match().deltaAsBlockPos()));
                Report.trigger(player, WizcraftReports.Worktable.INCOMPLETE);

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
        wand.attachTo(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Wizcraft.LOGGER.trace(user.getName() + " interrupted a wand usage");
        var wand = Wand.fromStack(stack);
        wand.getFocus().interrupt(world, wand, user, remainingUseTicks);
        wand.attachTo(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Wizcraft.LOGGER.trace(user.getName() + " finished using a wand");
        var wand = Wand.fromStack(stack);
        wand.getFocus().finish(world, wand, user);
        return wand.attachToCopyOf(stack);
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
                    KeyBindingHelper.getBoundKeyOf(WizcraftKeybindings.TOOL_CONTROL).getLocalizedText().getString()
                ).styled(style -> style.withColor(Formatting.GRAY).withItalic(true)));
    }
}
