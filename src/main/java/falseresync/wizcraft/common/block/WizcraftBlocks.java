package falseresync.wizcraft.common.block;

import falseresync.wizcraft.common.blockentity.ChargingWorktableBlockEntity;
import falseresync.wizcraft.common.blockentity.CraftingWorktableBlockEntity;
import falseresync.wizcraft.common.blockentity.WizcraftBlockEntities;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;

import java.util.function.Function;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftBlocks {
    public static final CrucibleBlock CRUCIBLE = r("crucible", CrucibleBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));

    public static final LensBlock LENS = r("lens", LensBlock::new, AbstractBlock.Settings.copy(Blocks.GLASS).luminance(state -> 1));
    public static final LensingPedestalBlock LENSING_PEDESTAL = r("lensing_pedestal", LensingPedestalBlock::new, AbstractBlock.Settings.copy(Blocks.BRICK_WALL));

    public static final AbstractBlock.Settings WORKTABLE_SETTINGS = AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE).requiresTool();
    public static final DummyWorktableBlock DUMMY_WORKTABLE = r("dummy_worktable", DummyWorktableBlock::new, WORKTABLE_SETTINGS);
    public static final WorktableBlock<CraftingWorktableBlockEntity> CRAFTING_WORKTABLE =
            rWorktable("crafting_worktable",
                    new WorktableBuilder<CraftingWorktableBlockEntity>()
                            .type(() -> WizcraftBlockEntities.CRAFTING_WORKTABLE)
                            .ticker(CraftingWorktableBlockEntity::tick)
                            .pattern(WorktablePatterns::crafting)
                            .build(),
                    WORKTABLE_SETTINGS);
    public static final WorktableBlock<ChargingWorktableBlockEntity> CHARGING_WORKTABLE =
            rWorktable("charging_worktable",
                    new WorktableBuilder<ChargingWorktableBlockEntity>()
                            .type(() -> WizcraftBlockEntities.CHARGING_WORKTABLE)
                            .ticker(ChargingWorktableBlockEntity::tick)
                            .pattern(WorktablePatterns::charging)
                            .build(),
                    WORKTABLE_SETTINGS);

    private static <T extends WorktableBlock<?>> T rWorktable(String id, Function<AbstractBlock.Settings, T> block, AbstractBlock.Settings settings) {
        var fullId = wid(id);
        var bakedBlock = block.apply(settings);
        WorktableVariant.register(bakedBlock.getVariant());
        return Registry.register(Registries.BLOCK, fullId, bakedBlock);
    }

    private static <T extends Block> T r(String id, Function<AbstractBlock.Settings, T> block, AbstractBlock.Settings settings) {
        var fullId = wid(id);
        return Registry.register(Registries.BLOCK, fullId, block.apply(settings));
    }

    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            if (!player.isSpectator() && player.getStackInHand(hand).isIn(WizcraftItemTags.ACTIVATORS)) {
                var pos = hitResult.getBlockPos();
                if (world.getBlockState(pos).isOf(Blocks.WATER_CAULDRON) && world.getBlockState(pos.down()).isOf(Blocks.FIRE)) {
                    world.setBlockState(pos, CRUCIBLE.getDefaultState(), Block.NOTIFY_ALL);
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }
}
