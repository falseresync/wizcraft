package dev.falseresync.wizcraft.datafixer;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class WizcraftServerDataFix implements DedicatedServerModInitializer {
    @Nullable
    public static ServerWorld cachedWorld;

    @Override
    public void onInitializeServer() {
        ServerWorldEvents.LOAD.register(((server, world) -> {
            if (server instanceof DedicatedServer) {
                cachedWorld = world;
            }
        }));
    }
}
