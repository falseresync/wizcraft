package falseresync.wizcraft.client.render.blockentity;

import falseresync.wizcraft.client.render.*;
import falseresync.wizcraft.common.*;
import falseresync.wizcraft.common.blockentity.*;
import falseresync.wizcraft.common.data.attachment.*;
import net.minecraft.client.*;
import net.minecraft.client.font.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.fluid.*;
import net.minecraft.util.math.*;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final TextRenderer textRenderer;
    private final MinecraftClient client;

    public CrucibleRenderer(BlockEntityRendererFactory.Context ctx) {
        itemRenderer = ctx.getItemRenderer();
        textRenderer = ctx.getTextRenderer();
        client = MinecraftClient.getInstance();
    }

    @Override
    public void render(CrucibleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (client.player != null
                && client.player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)
                && client.player.getPos().isInRange(entity.getPos().toCenterPos(), Wizcraft.getConfig().trueseerGogglesDisplayRange)) {
            var stacks = entity.getInventory().getHeldStacks();
            matrices.push();
            matrices.translate(0.5, 1.25, 0.5);
            for (int i = 0; i < stacks.size(); i++) {
                var stack = stacks.get(i);
                if (stack.isEmpty()) {
                    continue;
                }

                matrices.push();
                matrices.translate(0.1, 0.5 * i, 0);
                matrices.multiply(client.gameRenderer.getCamera().getRotation());

                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                var textScale = 0.5f /* block sizing [-0.5,0.5] */ * 1 / 9f /* text sizing [1,9] */ * 0.3f /* actual scale */;
                matrices.scale(textScale, textScale, textScale);
                textRenderer.draw(
                        stack.getCount() + "", 16, -4, 0xFF_FF_FF, true,
                        matrices.peek().getPositionMatrix(), vertexConsumers,
                        TextRenderer.TextLayerType.SEE_THROUGH, 0, light);
                matrices.pop();

                matrices.scale(0.4f, 0.4f, 0.4f);
                itemRenderer.renderItem(stack, ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, client.world, 0);

                matrices.pop();
            }
            matrices.pop();
        }

        matrices.push();
        matrices.translate(0, 1, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        RenderingUtil.drawFluidOnBlockEntity(
                matrices, vertexConsumers.getBuffer(RenderLayer.getTranslucent()),
                entity.getWorld(), entity.getPos(), Fluids.WATER, Fluids.WATER.getDefaultState(), true,
                light, overlay, 0.125f, 0.75f, 0.125f, 0.75f, 0.125f);
        matrices.pop();
    }
}
