package ru.falseresync.wizcraft.lib.worldevents.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.falseresync.wizcraft.lib.worldevents.WorldEventUtil;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private ClientWorld world;

    @Inject(method = "processGlobalEvent", at = @At("HEAD"))
    private void lib_processGlobalEvents(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (eventId > WorldEventUtil.PREFIX) {
            WorldEventUtil.getGlobal(eventId).process(client, world, ((ClientWorldAccessor) world).getWorldRenderer(), pos, data);
        }
    }

    @Inject(method = "processWorldEvent", at = @At("HEAD"))
    private void lib_processLocalEvents(int eventId, BlockPos pos, int data, CallbackInfo ci) {
        if (eventId > WorldEventUtil.PREFIX) {
            WorldEventUtil.getLocal(eventId).process(client, world, ((ClientWorldAccessor) world).getWorldRenderer(), pos, data);
        }
    }
}
