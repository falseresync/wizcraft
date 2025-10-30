package falseresync.wizcraft.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import falseresync.wizcraft.client.render.RenderingUtil;
import falseresync.wizcraft.common.Wizcraft;
import falseresync.wizcraft.common.blockentity.CrucibleBlockEntity;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.material.Fluids;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final Font textRenderer;
    private final Minecraft client;

    public CrucibleRenderer(BlockEntityRendererProvider.Context ctx) {
        itemRenderer = ctx.getItemRenderer();
        textRenderer = ctx.getFont();
        client = Minecraft.getInstance();
    }

    @Override
    public void render(CrucibleBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (client.player != null
                && client.player.hasAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES)
                && client.player.position().closerThan(entity.getBlockPos().getCenter(), Wizcraft.getConfig().trueseerGogglesDisplayRange)) {
            var stacks = entity.getInventory().getItems();
            matrices.pushPose();
            matrices.translate(0.5, 1.25, 0.5);
            for (int i = 0; i < stacks.size(); i++) {
                var stack = stacks.get(i);
                if (stack.isEmpty()) {
                    continue;
                }

                matrices.pushPose();
                matrices.translate(0.1, 0.5 * i, 0);
                matrices.mulPose(client.gameRenderer.getMainCamera().rotation());

                matrices.pushPose();
                matrices.mulPose(Axis.XP.rotationDegrees(180));
                var textScale = 0.5f /* block sizing [-0.5,0.5] */ * 1 / 9f /* text sizing [1,9] */ * 0.3f /* actual scale */;
                matrices.scale(textScale, textScale, textScale);
                textRenderer.drawInBatch(
                        stack.getCount() + "", 16, -4, 0xFF_FF_FF, true,
                        matrices.last().pose(), vertexConsumers,
                        Font.DisplayMode.SEE_THROUGH, 0, light);
                matrices.popPose();

                matrices.scale(0.4f, 0.4f, 0.4f);
                itemRenderer.renderStatic(stack, ItemDisplayContext.GUI, light, overlay, matrices, vertexConsumers, client.level, 0);

                matrices.popPose();
            }
            matrices.popPose();
        }

        matrices.pushPose();
        matrices.translate(0, 1, 0);
        matrices.mulPose(Axis.XP.rotationDegrees(90));
        RenderingUtil.drawFluid(
                matrices, vertexConsumers.getBuffer(RenderType.translucent()),
                entity.getLevel(), entity.getBlockPos(), Fluids.WATER, Fluids.WATER.defaultFluidState(), true,
                light, overlay, 0.125f, 0.75f, 0.125f, 0.75f, 0.125f);
        matrices.popPose();
    }
}
