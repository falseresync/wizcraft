package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlockTags;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class WizcraftBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public WizcraftBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(WizcraftBlocks.DUMMY_WORKTABLE)
                .add(WizcraftBlocks.CRAFTING_WORKTABLE)
                .add(WizcraftBlocks.CHARGING_WORKTABLE);
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(WizcraftBlocks.CRUCIBLE)
                .add(WizcraftBlocks.LENS)
                .add(WizcraftBlocks.DUMMY_WORKTABLE)
                .add(WizcraftBlocks.CRAFTING_WORKTABLE)
                .add(WizcraftBlocks.CHARGING_WORKTABLE)
                .add(WizcraftBlocks.LENSING_PEDESTAL);
        getOrCreateTagBuilder(WizcraftBlockTags.CRUCIBLE_HEAT_SOURCES)
                .add(Blocks.FIRE)
                .add(Blocks.SOUL_FIRE)
                .forceAddTag(BlockTags.CAMPFIRES);
    }
}
