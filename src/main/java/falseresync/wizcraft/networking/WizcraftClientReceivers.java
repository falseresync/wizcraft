package falseresync.wizcraft.networking;

import falseresync.wizcraft.client.*;
import falseresync.wizcraft.networking.s2c.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.client.world.*;

public class WizcraftClientReceivers {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerBlockPatternTipS2CPayload.ID, WizcraftClientReceivers::triggerBlockPatternTip);
    }

    private static void triggerBlockPatternTip(TriggerBlockPatternTipS2CPayload payload, ClientPlayNetworking.Context context) {
        BlockPatternTip.spawnCompletionTipParticles(context.player(), (ClientWorld) context.player().getWorld(), payload.missingBlocks());
    }
}
