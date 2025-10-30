package falseresync.wizcraft.networking;

import falseresync.wizcraft.networking.c2s.*;
import falseresync.wizcraft.networking.s2c.*;
import net.fabricmc.fabric.api.networking.v1.*;

public class WizcraftNetworking {
    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(TriggerBlockPatternTipS2CPayload.ID, TriggerBlockPatternTipS2CPayload.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(ChangeWandFocusC2SPayload.ID, ChangeWandFocusC2SPayload.PACKET_CODEC);
    }
}