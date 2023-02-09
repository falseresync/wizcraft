package ru.falseresync.wizcraft.lib.worldevents;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface WorldEventProcessor {
    @Environment(EnvType.CLIENT)
    void process(MinecraftClient client, ClientWorld world, WorldRenderer renderer, BlockPos pos, int data);
}
