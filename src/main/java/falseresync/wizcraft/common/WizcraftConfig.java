package falseresync.wizcraft.common;

import eu.midnightdust.lib.config.MidnightConfig;

public final class WizcraftConfig extends MidnightConfig {
    @Entry(category = "cheats")
    public static boolean expendWandChargeInSurvival = true;
    @Entry(category = "cheats")
    public static boolean expendWandChargeInCreative = false;
}