package dev.falseresync.wizcraft.common.report;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.report.focuses.cometwarp.CometWarpAnchorPlacedReport;
import dev.falseresync.wizcraft.common.report.focuses.cometwarp.CometWarpNoAnchorReport;
import dev.falseresync.wizcraft.common.report.focuses.cometwarp.CometWarpTeleportedReport;
import dev.falseresync.wizcraft.common.report.wand.*;
import dev.falseresync.wizcraft.common.report.worktable.*;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WizcraftReports {
    private static final Map<Identifier, Report> TO_REGISTER = new HashMap<>();

    private static <T extends Report> T r(T report) {
        TO_REGISTER.put(report.getId(), report);
        return report;
    }

    public static void register(BiConsumer<Identifier, Report> registrar) {
        Worktable.init();
        Wand.init();
        Focuses.init();
        TO_REGISTER.forEach(registrar);
    }

    public static final class Worktable {
        public static final WorktableIncompleteReport INCOMPLETE = r(new WorktableIncompleteReport());
        public static final WorktableCannotPlaceReport CANNOT_PLACE = r(new WorktableCannotPlaceReport());
        public static final WorktableSuccessReport SUCCESS = r(new WorktableSuccessReport());
        public static final WorktableInterruptedReport INTERRUPTED = r(new WorktableInterruptedReport());
        public static final WorktableCraftingReport CRAFTING = r(new WorktableCraftingReport());

        private static void init() {
        }
    }

    public static final class Wand {
        public static final WandCannotChargeReport CANNOT_CHARGE = r(new WandCannotChargeReport());
        public static final WandAlreadyFullyChargedReport ALREADY_FULLY_CHARGED = r(new WandAlreadyFullyChargedReport());
        public static final WandSuccessfullyChargedReport SUCCESSFULLY_CHARGED = r(new WandSuccessfullyChargedReport());
        public static final WandInsufficientChargeReport INSUFFICIENT_CHARGE = r(new WandInsufficientChargeReport());

        private static void init() {
        }
    }

    public static final class Focuses {
        public static final CometWarpNoAnchorReport NO_ANCHOR = r(new CometWarpNoAnchorReport());
        public static final CometWarpAnchorPlacedReport ANCHOR_PLACED = r(new CometWarpAnchorPlacedReport());
        public static final CometWarpTeleportedReport TELEPORTED = r(new CometWarpTeleportedReport());

        private static void init() {
        }
    }
}
