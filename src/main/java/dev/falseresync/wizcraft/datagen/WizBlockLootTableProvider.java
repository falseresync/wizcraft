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
        addDrop(WizBlocks.LENS, drops(WizItems.LENS));
        addDrop(WizBlocks.WORKTABLE, drops(WizItems.WORKTABLE));
        addDrop(WizBlocks.LENSING_PEDESTAL, drops(WizItems.LENSING_PEDESTAL));
    }
}
