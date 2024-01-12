package dev.falseresync.wizcraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class RenderingUtil {
    public static Vec3d getSymmetricVec3d(double value) {
        return new Vec3d(value, value, value);
    }
}
