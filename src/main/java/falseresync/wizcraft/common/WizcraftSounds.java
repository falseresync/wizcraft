package falseresync.wizcraft.common;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftSounds {
    public static final SoundEvent WORKTABLE_SUCCESS = r("block.worktable.success");
    public static final SoundEvent STAR_PROJECTILE_EXPLODE = r("entity.star_projectile.explode");
    public static final SoundEvent COMET_WARP_ANCHOR_PLACED = r("focus.comet_warp.anchor_placed");
    public static final SoundEvent SUCCESSFULLY_CHARGED = r("focus.charging.successfully_charged");

    private static SoundEvent r(String id) {
        var fullId = wid(id);
        return Registry.register(Registries.SOUND_EVENT, fullId, SoundEvent.of(fullId));
    }

    public static void init() {
    }
}
