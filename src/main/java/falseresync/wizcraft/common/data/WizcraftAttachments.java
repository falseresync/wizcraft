package falseresync.wizcraft.common.data;

import com.mojang.serialization.*;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.network.codec.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftAttachments {
    public static final AttachmentType<Boolean> INTRODUCED_TO_WIZCRAFT = AttachmentRegistry.create(
            wid("introduced_to_wizcraft"),
            builder -> builder.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly()).persistent(Codec.BOOL).copyOnDeath());
    public static final AttachmentType<Boolean> HAS_TRUESEER_GOGGLES = AttachmentRegistry.create(
            wid("has_trueseer_goggles"),
            builder -> builder.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly()).persistent(Codec.BOOL));
    public static final AttachmentType<Boolean> THUNDERLESS_LIGHTNING = AttachmentRegistry.create(
            wid("thunderless_lightning"),
            builder -> builder.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.all()));
    public static final AttachmentType<Integer> ENERGY_VEIL_NETWORK_ID = AttachmentRegistry.create(
            wid("energy_veil_id"),
            builder -> builder.syncWith(PacketCodecs.INTEGER, AttachmentSyncPredicate.all()));
    public static final AttachmentType<ChargeShellsAttachment> CHARGE_SHELLS = AttachmentRegistry.create(
            wid("charge_shells"),
            builder -> builder
                    .initializer(ChargeShellsAttachment::createDefault)
                    .syncWith(ChargeShellsAttachment.PACKET_CODEC, AttachmentSyncPredicate.targetOnly())
                    .persistent(ChargeShellsAttachment.CODEC));

    public static void init() {
    }
}
