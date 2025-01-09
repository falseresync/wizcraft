package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.block.WizcraftBlocks;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WizcraftBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected WizcraftBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        addDrop(WizcraftBlocks.CRUCIBLE, drops(WizcraftItems.CRUCIBLE));
        addDrop(WizcraftBlocks.LENS, drops(WizcraftItems.LENS));
        addDrop(WizcraftBlocks.DUMMY_WORKTABLE, drops(WizcraftItems.WORKTABLE));
        addDrop(WizcraftBlocks.CRAFTING_WORKTABLE, drops(WizcraftItems.WORKTABLE));
        addDrop(WizcraftBlocks.CHARGING_WORKTABLE, drops(WizcraftItems.WORKTABLE));
        addDrop(WizcraftBlocks.LENSING_PEDESTAL, drops(WizcraftItems.LENSING_PEDESTAL));
    }
}
