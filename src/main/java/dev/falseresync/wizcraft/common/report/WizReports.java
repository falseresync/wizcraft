package dev.falseresync.wizcraft.common.report;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.common.report.lensedworktable.InvalidPedestalFormationReport;
import dev.falseresync.wizcraft.common.report.lensedworktable.LensedWorktableSuccessAreaReport;
import dev.falseresync.wizcraft.common.report.skywand.AlreadyFullyChargedReport;
import dev.falseresync.wizcraft.common.report.skywand.CannotChargeReport;
import dev.falseresync.wizcraft.common.report.skywand.SuccessfullyChargedReport;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WizReports {
    private static final Map<Identifier, Report> TO_REGISTER = new HashMap<>();
    public static final InvalidPedestalFormationReport INVALID_PEDESTAL_FORMATION = r(new InvalidPedestalFormationReport());
    public static final LensedWorktableSuccessAreaReport SUCCESS = r(new LensedWorktableSuccessAreaReport());
    public static final SuccessfullyChargedReport SUCCESSFULLY_CHARGED = r(new SuccessfullyChargedReport());
    public static final CannotChargeReport CANNOT_CHARGE = r(new CannotChargeReport());
    public static final AlreadyFullyChargedReport ALREADY_FULLY_CHARGED = r(new AlreadyFullyChargedReport());

    private static <T extends Report> T r(T report) {
        TO_REGISTER.put(report.getId(), report);
        return report;
    }

    public static void register(BiConsumer<Identifier, Report> registrar) {
        TO_REGISTER.forEach(registrar);
    }
}
