package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.block.WizBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class WizBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public WizBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(WizBlocks.DUMMY_WORKTABLE)
                .add(WizBlocks.CRAFTING_WORKTABLE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(WizBlocks.LENS)
                .add(WizBlocks.DUMMY_WORKTABLE)
                .add(WizBlocks.CRAFTING_WORKTABLE)
                .add(WizBlocks.LENSING_PEDESTAL);
    }
}
