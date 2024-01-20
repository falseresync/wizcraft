package dev.falseresync.wizcraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class RenderingUtil {
    public static final Vec3d UNIT_VEC3D = RenderingUtil.getSymmetricVec3d(1);

    public static Vec3d getSymmetricVec3d(double value) {
        return new Vec3d(value, value, value);
    }
}
