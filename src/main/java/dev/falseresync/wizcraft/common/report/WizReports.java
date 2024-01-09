package dev.falseresync.wizcraft.common.report;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.report.focuses.AnchorPlacedReport;
import dev.falseresync.wizcraft.common.report.focuses.NoAnchorReport;
import dev.falseresync.wizcraft.common.report.focuses.TeleportedReport;
import dev.falseresync.wizcraft.common.report.wand.*;
import dev.falseresync.wizcraft.common.report.worktable.InvalidPedestalFormationReport;
import dev.falseresync.wizcraft.common.report.worktable.WorktableCraftingReport;
import dev.falseresync.wizcraft.common.report.worktable.WorktableInterruptedReport;
import dev.falseresync.wizcraft.common.report.worktable.WorktableSuccessReport;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WizReports {
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
        public static final InvalidPedestalFormationReport INVALID_PEDESTAL_FORMATION = r(new InvalidPedestalFormationReport());
        public static final WorktableSuccessReport SUCCESS = r(new WorktableSuccessReport());
        public static final WorktableInterruptedReport INTERRUPTED = r(new WorktableInterruptedReport());
        public static final WorktableCraftingReport CRAFTING = r(new WorktableCraftingReport());

        private static void init() {
        }
    }

    public static final class Wand {
        public static final CannotChargeReport CANNOT_CHARGE = r(new CannotChargeReport());
        public static final AlreadyFullyChargedReport ALREADY_FULLY_CHARGED = r(new AlreadyFullyChargedReport());
        public static final SuccessfullyChargedReport SUCCESSFULLY_CHARGED = r(new SuccessfullyChargedReport());
        public static final InsufficientChargeReport INSUFFICIENT_CHARGE = r(new InsufficientChargeReport());

        private static void init() {
        }
    }

    public static final class Focuses {
        public static final NoAnchorReport NO_ANCHOR = r(new NoAnchorReport());
        public static final AnchorPlacedReport ANCHOR_PLACED = r(new AnchorPlacedReport());
        public static final TeleportedReport TELEPORTED = r(new TeleportedReport());

        private static void init() {
        }
    }
}
