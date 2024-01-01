package dev.falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.entity.EnergizedWorktableBlockEntity;
import dev.falseresync.wizcraft.common.block.entity.WizBlockEntities;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.lib.HasId;
import dev.falseresync.wizcraft.lib.WizUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnergizedWorktableBlock extends BlockWithEntity implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "energized_worktable");
    public static final MapCodec<EnergizedWorktableBlock> CODEC = createCodec(EnergizedWorktableBlock::new);

    protected EnergizedWorktableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnergizedWorktableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, WizBlockEntities.ENERGIZED_WORKTABLE, EnergizedWorktableBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof EnergizedWorktableBlockEntity worktable) {
            var playerStack = player.getMainHandStack();
            if (playerStack.isOf(WizItems.SKY_WAND)) {
                worktable.craft(player);
                return ActionResult.SUCCESS;
            }

            var exchanged = WizUtils.exchangeStackInSlotWithHand(player, hand, worktable.storage, 0, 1, null);
            if (exchanged == 1) {
                return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}