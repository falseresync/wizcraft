package falseresync.wizcraft.client.render.item;

import net.minecraft.client.network.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class ChargingFocusUseAction {
    public static void applyFirstPersonTransformation(HeldItemRendererContext context) {
        int i = context.arm == Arm.RIGHT ? 1 : -1;
        context.matrices.translate((float) i * 0.56F, -0.72F + context.equipProgress * -0.6F, -0.72F);
    }

    public static void positionArm(BipedEntityModelContext context) {
        context.model.rightArm.pitch = context.model.rightArm.pitch * 0.5F - (float) Math.PI;
        context.model.rightArm.yaw = 0.0F;
    }

    public static boolean shouldSkipDefaultArmPositioning(BipedEntityModelContext context) {
        return false;
    }

    public static boolean shouldNeverSwingArm(BipedEntityModelContext context) {
        return false;
    }

    public static boolean isTwoHanded(BipedEntityModelContext context) {
        return false;
    }

    public record HeldItemRendererContext(
            AbstractClientPlayerEntity player,
            ItemStack stack,
            Hand hand,
            Arm arm,
            float pitch,
            float swingProgress,
            float equipProgress,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            float tickDelta,
            int light
    ) {
    }

    public record BipedEntityModelContext(
            BipedEntityModel<LivingEntity> model, LivingEntity entity, Arm arm, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch
    ) {
    }
}
