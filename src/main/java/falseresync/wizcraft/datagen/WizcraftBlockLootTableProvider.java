package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class WizcraftBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected WizcraftBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        add(WizcraftBlocks.CRUCIBLE, createSingleItemTable(WizcraftItems.CRUCIBLE));
        add(WizcraftBlocks.LENS, createSingleItemTable(WizcraftItems.LENS));
        add(WizcraftBlocks.DUMMY_WORKTABLE, createSingleItemTable(WizcraftItems.WORKTABLE));
        add(WizcraftBlocks.CRAFTING_WORKTABLE, createSingleItemTable(WizcraftItems.WORKTABLE));
        add(WizcraftBlocks.CHARGING_WORKTABLE, createSingleItemTable(WizcraftItems.WORKTABLE));
        add(WizcraftBlocks.LENSING_PEDESTAL, createSingleItemTable(WizcraftItems.LENSING_PEDESTAL));
    }
}
