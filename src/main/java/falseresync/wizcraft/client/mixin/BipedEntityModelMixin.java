package falseresync.wizcraft.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.*;
import falseresync.wizcraft.client.render.item.*;
import falseresync.wizcraft.common.item.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin {
    @WrapOperation(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;positionRightArm(Lnet/minecraft/entity/LivingEntity;)V"))
    public void wizcraft$positionRightArm(BipedEntityModel<LivingEntity> instance,
                                          LivingEntity entity,
                                          Operation<Void> original,
                                          @Local(argsOnly = true, ordinal = 0) float limbAngle,
                                          @Local(argsOnly = true, ordinal = 1) float limbDistance,
                                          @Local(argsOnly = true, ordinal = 2) float animationProgress,
                                          @Local(argsOnly = true, ordinal = 3) float headYaw,
                                          @Local(argsOnly = true, ordinal = 4) float headPitch) {
        if (entity.isUsingItem()) {
            var context = new ChargingFocusUseAction.BipedEntityModelContext(
                    instance, entity, Arm.RIGHT, limbAngle, limbDistance, animationProgress, headYaw, headPitch
            );

            if (!ChargingFocusUseAction.shouldSkipDefaultArmPositioning(context)) {
                original.call(instance, entity);
            }

            var stack = entity.getMainHandStack();
            if (stack.isOf(WizcraftItems.WAND) && WizcraftItems.WAND.getEquipped(stack).isOf(WizcraftItems.CHARGING_FOCUS)) {
                ChargingFocusUseAction.positionArm(context);
            }
        } else {
            original.call(instance, entity);
        }
    }
}
