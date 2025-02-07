package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlockTags;
import falseresync.wizcraft.common.block.WizcraftBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class WizcraftBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public WizcraftBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(WizcraftBlocks.DUMMY_WORKTABLE)
                .add(WizcraftBlocks.CRAFTING_WORKTABLE)
                .add(WizcraftBlocks.CHARGING_WORKTABLE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
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
