package falseresync.wizcraft.common;

import eu.midnightdust.lib.config.MidnightConfig;

public final class WizcraftConfig extends MidnightConfig {
    @Entry(category = "cheats")
    public static boolean freeChargeInSurvival = false;
    @Entry(category = "cheats")
    public static boolean freeChargeInCreative = true;

    @Entry(category = "performance", min = 3)
    public static int trueseerGogglesDisplayRange = 10;
}