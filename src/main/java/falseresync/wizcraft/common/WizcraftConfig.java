package falseresync.wizcraft.common;

import eu.midnightdust.lib.config.MidnightConfig;

public final class WizcraftConfig extends MidnightConfig {
    @Entry(category = "cheats")
    public static boolean freeChargeInSurvival = false;
    @Entry(category = "cheats")
    public static boolean freeChargeInCreative = true;

    @Entry(category = "performance", min = 3, max = 32, isSlider = true)
    public static int trueseerGogglesDisplayRange = 10;
    @Entry(category = "performance")
    public static ParticlesAmountModifier animationParticlesAmount = ParticlesAmountModifier.DEFAULT;
    public enum ParticlesAmountModifier {
        REDUCED(0.6f), DEFAULT(1);

        public final float modifier;

        ParticlesAmountModifier(float modifier) {
            this.modifier = modifier;
        }
    }
}