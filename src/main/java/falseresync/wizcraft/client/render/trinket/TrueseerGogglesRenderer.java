package falseresync.wizcraft.client.render.trinket;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TrueseerGogglesRenderer implements TrinketRenderer {
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float limbAngle, float limbDistance, float partialTick, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayer player) {
            poseStack.pushPose();
            // Since this is a player, the model also has to be one of a player
            //noinspection unchecked
            TrinketRenderer.translateToFace(poseStack, (PlayerModel<AbstractClientPlayer>) contextModel, player, headYaw, headPitch);
            poseStack.scale(-0.6f, -0.6f, 1);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, 0, poseStack, bufferSource, null, 0);
            poseStack.popPose();
        }
    }
}
