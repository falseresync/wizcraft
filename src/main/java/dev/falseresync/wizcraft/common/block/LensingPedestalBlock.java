package dev.falseresync.wizcraft.common.block;

import com.mojang.serialization.MapCodec;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.entity.LensingPedestalBlockEntity;
import dev.falseresync.wizcraft.lib.HasId;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LensingPedestalBlock extends BlockWithEntity implements HasId {
    public static final Identifier ID = new Identifier(Wizcraft.MODID, "lensing_pedestal");
    public static final MapCodec<LensingPedestalBlock> CODEC = createCodec(LensingPedestalBlock::new);

    protected LensingPedestalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<LensingPedestalBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LensingPedestalBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof LensingPedestalBlockEntity pedestal) {
            var stack = player.getStackInHand(hand);
            var resource = ItemVariant.of(stack);
            var tx = Transaction.openOuter();
            if (!resource.isBlank()) {
                try (tx) {
                    var inserted = pedestal.storage.insert(resource, 1, tx) == 1;
                    var extracted = PlayerInventoryStorage.of(player).extract(resource, 1, tx) == 1;
                    if (inserted & extracted) {
                        tx.commit();
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
