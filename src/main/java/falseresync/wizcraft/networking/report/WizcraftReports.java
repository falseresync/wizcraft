package falseresync.wizcraft.networking.report;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftReports {
    public static final SimpleRegistry<Report> REGISTRY =
            FabricRegistryBuilder
                    .<Report>createSimple(RegistryKey.ofRegistry(wid("reports")))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final @RegistryObject WandCannotChargeReport WAND_CANNOT_CHARGE = new WandCannotChargeReport();
    public static final @RegistryObject WandAlreadyFullyChargedReport WAND_ALREADY_FULLY_CHARGED = new WandAlreadyFullyChargedReport();
    public static final @RegistryObject WandSuccessfullyChargedReport WAND_SUCCESSFULLY_CHARGED = new WandSuccessfullyChargedReport();
    public static final @RegistryObject WandInsufficientChargeReport WAND_INSUFFICIENT_CHARGE = new WandInsufficientChargeReport();

    public static final @RegistryObject CometWarpNoAnchorReport COMET_WARP_NO_ANCHOR = new CometWarpNoAnchorReport();
    public static final @RegistryObject CometWarpAnchorPlacedReport COMET_WARP_ANCHOR_PLACED = new CometWarpAnchorPlacedReport();
    public static final @RegistryObject CometWarpTeleportedReport COMET_WARP_TELEPORTED = new CometWarpTeleportedReport();

    public static final @RegistryObject WorktableIncompleteReport WORKTABLE_INCOMPLETE = new WorktableIncompleteReport();
    public static final @RegistryObject WorktableCannotPlaceReport WORKTABLE_CANNOT_PLACE = new WorktableCannotPlaceReport();
    public static final @RegistryObject WorktableSuccessReport WORKTABLE_SUCCESS = new WorktableSuccessReport();
    public static final @RegistryObject WorktableInterruptedReport WORKTABLE_INTERRUPTED = new WorktableInterruptedReport();
    public static final @RegistryObject WorktableCraftingReport WORKTABLE_CRAFTING = new WorktableCraftingReport();

}
