package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.block.WizcraftBlocks;
import dev.falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WizBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected WizBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        addDrop(WizcraftBlocks.LENS, drops(WizcraftItems.LENS));
        addDrop(WizcraftBlocks.DUMMY_WORKTABLE, drops(WizcraftItems.WORKTABLE));
        addDrop(WizcraftBlocks.CRAFTING_WORKTABLE, drops(WizcraftItems.WORKTABLE));
        addDrop(WizcraftBlocks.CHARGING_WORKTABLE, drops(WizcraftItems.WORKTABLE));
        addDrop(WizcraftBlocks.LENSING_PEDESTAL, drops(WizcraftItems.LENSING_PEDESTAL));
    }
}
