package ru.falseresync.wizcraft.client.render.block.entity;

import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import ru.falseresync.wizcraft.common.Wizcraft;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;

import java.util.Collections;
import java.util.List;

public class MagicCauldronRenderer implements BlockEntityRenderer<MagicCauldronBlockEntity> {
    private static final List<FluidRenderFace> FACES = Collections.singletonList(FluidRenderFace.createFlatFace(2 / 16d, 14 / 16d, 2 / 16d, 14 / 16d, 14 / 16d, 14 / 16d, 1, Direction.UP));

    public MagicCauldronRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MagicCauldronBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var fluidVolume = entity.fluidInv.getInvFluid(0);
        if (fluidVolume.isEmpty()) {
            return;
        }

        fluidVolume.render(FACES, vertexConsumers, matrices);
    }
}
