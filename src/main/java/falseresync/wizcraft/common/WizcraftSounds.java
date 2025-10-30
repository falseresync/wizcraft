package falseresync.wizcraft.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftSounds {
    public static final SoundEvent WORKTABLE_SUCCESS = r("block.worktable.success");
    public static final SoundEvent STAR_PROJECTILE_EXPLODE = r("entity.star_projectile.explode");
    public static final SoundEvent COMET_WARP_ANCHOR_PLACED = r("focus.comet_warp.anchor_placed");
    public static final SoundEvent SUCCESSFULLY_CHARGED = r("focus.charging.successfully_charged");

    private static SoundEvent r(String id) {
        var fullId = wid(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, fullId, SoundEvent.createVariableRangeEvent(fullId));
    }

    public static void init() {
    }
}
