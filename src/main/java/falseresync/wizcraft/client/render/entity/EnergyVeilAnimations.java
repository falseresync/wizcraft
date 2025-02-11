package falseresync.wizcraft.client.render.entity;

import net.minecraft.client.render.entity.animation.*;
import org.joml.Vector3f;

// Behold stupidity^meth: ModelPart.scale(Vec3) adds the scale vector, not multiplies it
// Why?
// What for?
// This is so wrong
public class EnergyVeilAnimations {
    // TODO: add other screens
    public static final Animation SLIDE = Animation.Builder.create(2).looping()
            .addBoneAnimation("screen1", new Transformation(
                    Transformation.Targets.SCALE,
                    new Keyframe(0.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(0.500F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(1.500F, new Vector3f(-1.0F, -1.0F, -1.0F), Transformation.Interpolations.LINEAR),
                    new Keyframe(2.000F, new Vector3f(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
            ))
            .build();
}
