package dev.falseresync.wizcraft.common;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WizcraftSounds {
    private static final Map<Identifier, SoundEvent> TO_REGISTER = new HashMap<>();

    private static SoundEvent r(String id) {
        var sound = SoundEvent.of(new Identifier(Wizcraft.MOD_ID, id));
        TO_REGISTER.put(sound.getId(), sound);
        return sound;
    }

    public static void register(BiConsumer<Identifier, SoundEvent> registrar) {
        Block.init();
        Entity.init();
        Focus.init();
        TO_REGISTER.forEach(registrar);
    }

    public static final class Block {
        public static final SoundEvent WORKTABLE_SUCCESS = r("block.worktable.success");

        private static void init() {
        }
    }

    public static final class Entity {
        public static final SoundEvent STAR_PROJECTILE_EXPLODE = r("entity.star_projectile.explode");

        private static void init() {
        }
    }

    public static final class Focus {
        public static final SoundEvent COMET_WARP_ANCHOR_PLACED = r("focus.comet_warp.anchor_placed");
        public static final SoundEvent SUCCESSFULLY_CHARGED = r("focus.charging.successfully_charged");

        private static void init() {
        }
    }
}
