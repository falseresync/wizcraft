package falseresync.wizcraft.client.render.trinket;

import dev.emi.trinkets.api.*;
import dev.emi.trinkets.api.client.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;

public class TrueseerGogglesRenderer implements TrinketRenderer {
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            matrices.push();
            // Since this is a player, the model also has to be one of a player
            //noinspection unchecked
            TrinketRenderer.translateToFace(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player, headYaw, headPitch);
            matrices.scale(-0.6f, -0.6f, 1);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, light, 0, matrices, vertexConsumers, null, 0);
            matrices.pop();
        }
    }
}
