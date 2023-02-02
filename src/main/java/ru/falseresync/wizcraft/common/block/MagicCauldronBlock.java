package ru.falseresync.wizcraft.common.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInvUtil;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemInvUtil;
import alexiil.mc.lib.attributes.item.entity.ItemTransferableItemEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;

public class MagicCauldronBlock extends BlockWithEntity implements AttributeProvider {
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
        if (entity instanceof ItemEntity itemEntity
                && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox()), INSIDE_SHAPE.offset(pos.getX(), pos.getY(), pos.getZ()), BooleanBiFunction.AND)) {
            if (world.getBlockEntity(pos) instanceof MagicCauldronBlockEntity blockEntity) {
                ItemInvUtil.move(new ItemTransferableItemEntity(itemEntity), blockEntity.itemInv, itemEntity.getStack().getMaxCount());
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof MagicCauldronBlockEntity blockEntity) {
            return FluidInvUtil.interactHandWithTank(blockEntity.fluidInv.getTransferable(), player, hand).asActionResult();
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    // AttributeProvider
    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        to.offer(FluidAttributes.INSERTABLE);
    }
}
