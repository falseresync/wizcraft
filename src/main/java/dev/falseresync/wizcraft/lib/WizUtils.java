package dev.falseresync.wizcraft.lib;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class WizUtils {
    public static int findViewDistance(World world) {
        return world.isClient()
                ? MinecraftClient.getInstance().options.getClampedViewDistance()
                : ((ServerWorld) world).getChunkManager().threadedAnvilChunkStorage.watchDistance;
    }
}
