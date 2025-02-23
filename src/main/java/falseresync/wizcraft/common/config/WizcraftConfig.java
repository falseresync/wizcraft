package falseresync.wizcraft.common.config;

import falseresync.wizcraft.common.*;
import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.annotation.*;

@Config(name = Wizcraft.MOD_ID)
@Config.Gui.Background(Config.Gui.Background.TRANSPARENT)
public final class WizcraftConfig implements ConfigData {
    @ConfigEntry.Category("general")
    @TranslatableEnum
    public InfiniteCharge infiniteCharge = InfiniteCharge.CREATIVE_ONLY;

    @ConfigEntry.Category("general")
    @TranslatableEnum
    public PassiveCharge passiveCharge = PassiveCharge.DEFAULT;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.CollapsibleObject
    public Transmutation transmutation = new Transmutation();

    public static class Transmutation {
        public int agingWeight = 30;
        public int transformationWeight = 15;
        public int doNothingWeight = 5;
    }

    @ConfigEntry.Category("performance")
    @ConfigEntry.BoundedDiscrete(min = 3, max = 32)
    public int trueseerGogglesDisplayRange = 10;

    @ConfigEntry.Category("performance")
    @TranslatableEnum
    public ParticlesAmount animationParticlesAmount = ParticlesAmount.DEFAULT;

    @ConfigEntry.Category("performance")
    @TranslatableEnum
    public AnimationQuality animationQuality = AnimationQuality.DEFAULT;

    @ConfigEntry.Category("accessibility")
    @TranslatableEnum
    public Transparency fullscreenEffectsTransparency = Transparency.DEFAULT;

    public enum InfiniteCharge {
        NEVER, CREATIVE_ONLY, ALWAYS;

        public boolean isCreativeOnly() {
            return this == CREATIVE_ONLY;
        }

        public boolean isAlways() {
            return this == ALWAYS;
        }
    }

    public enum ParticlesAmount {
        REDUCED(0.6f), DEFAULT(1);

        public final float modifier;

        ParticlesAmount(float modifier) {
            this.modifier = modifier;
        }
    }

    public enum AnimationQuality {
        FAST, DEFAULT
    }

    public enum Transparency {
        INCREASED(2f), DEFAULT(1);

        public final float modifier;

        Transparency(float modifier) {
            this.modifier = modifier;
        }
    }

    public enum PassiveCharge {
        DISABLED(0), DEFAULT(1), FASTER(2);

        public final float coefficient;

        PassiveCharge(float modifier) {
            this.coefficient = modifier;
        }
    }
}