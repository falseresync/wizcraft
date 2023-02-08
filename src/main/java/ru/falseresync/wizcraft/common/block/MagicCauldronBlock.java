package ru.falseresync.wizcraft.common.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;

public class MagicCauldronBlock extends BlockWithEntity implements FluidFillable {
    private static final VoxelShape INSIDE_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;

    static {
        INSIDE_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                VoxelShapes.fullCube(),
                VoxelShapes.union(
                        createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                        createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                        createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                        INSIDE_SHAPE),
                BooleanBiFunction.ONLY_FIRST);
    }

    public MagicCauldronBlock() {
        super(FabricBlockSettings.of(Material.METAL).mapColor(MapColor.STONE_GRAY).requiresTool().strength(2.0f).nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MagicCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, WizBlockEntities.MAGIC_CAULDRON, MagicCauldronBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return INSIDE_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && world.getBlockEntity(pos) instanceof MagicCauldronBlockEntity blockEntity
                && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox()), INSIDE_SHAPE.offset(pos.getX(), pos.getY(), pos.getZ()), BooleanBiFunction.AND)) {
            if (blockEntity.interactWithItemEntity(itemEntity)) {

                itemEntity.discard();
            }
        }
    }

    // This method is technically redundant because FluidFillable.tryFillWithFluid does the same
    // However many modders probably don't know or won't bother with supporting that
    // Hence this additional layer of supporting the things the Transfer-API-way is required
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof MagicCauldronBlockEntity blockEntity) {
            var success = FluidStorageUtil.interactWithFluidStorage(blockEntity.getFluidStorage(hit.getSide()), player, hand);
            return success ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return world.getBlockEntity(pos) instanceof MagicCauldronBlockEntity blockEntity && blockEntity.getFluidVariant().isBlank();
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof MagicCauldronBlockEntity blockEntity) {
            if (blockEntity.interactWithFluidState(fluidState)) {
                ((ServerWorld) world).getChunkManager().markForUpdate(pos);
            }
        }
        return true;
    }
}
