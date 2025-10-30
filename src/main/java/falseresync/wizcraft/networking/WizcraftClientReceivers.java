package falseresync.wizcraft.networking;

import falseresync.wizcraft.client.BlockPatternTip;
import falseresync.wizcraft.networking.s2c.TriggerBlockPatternTipS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;

public class WizcraftClientReceivers {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerBlockPatternTipS2CPayload.ID, WizcraftClientReceivers::triggerBlockPatternTip);
    }

    private static void triggerBlockPatternTip(TriggerBlockPatternTipS2CPayload payload, ClientPlayNetworking.Context context) {
        BlockPatternTip.spawnCompletionTipParticles(context.player(), (ClientLevel) context.player().level(), payload.missingBlocks());
    }
}
