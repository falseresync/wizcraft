package dev.falseresync.wizcraft.datagen;

import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.item.WizItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class WizBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected WizBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(WizBlocks.ENERGIZED_WORKTABLE, drops(WizItems.ENERGIZED_WORKTABLE));
        addDrop(WizBlocks.LENSING_PEDESTAL, drops(WizItems.LENSING_PEDESTAL));
    }
}
