package falseresync.wizcraft.common.data.attachment;

import com.mojang.serialization.*;
import falseresync.wizcraft.common.item.*;
import net.fabricmc.fabric.api.attachment.v1.*;
import net.minecraft.network.codec.*;
import net.minecraft.util.dynamic.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftAttachments {
    public static final AttachmentType<Boolean> INTRODUCED_TO_WIZCRAFT = AttachmentRegistry.create(
            wid("introduced_to_wizcraft"),
            builder -> builder.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly()).persistent(Codec.BOOL).copyOnDeath());
    public static final AttachmentType<Boolean> HAS_TRUESEER_GOGGLES = AttachmentRegistry.create(
            wid("has_trueseer_goggles"),
            builder -> builder.syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly()).persistent(Codec.BOOL));
    public static final AttachmentType<Integer> ENERGY_VEIL_NETWORK_ID = AttachmentRegistry.create(
            wid("energy_veil_id"),
            builder -> builder.syncWith(PacketCodecs.INTEGER, AttachmentSyncPredicate.all()));
    public static final AttachmentType<Integer> CHARGE_IN_SHELLS = AttachmentRegistry.create(
            wid("charge_in_shells"),
            builder -> builder
                    .initializer(() -> 0)
                    .syncWith(PacketCodecs.INTEGER, AttachmentSyncPredicate.targetOnly())
                    .persistent(Codecs.NONNEGATIVE_INT));
    public static final AttachmentType<Integer> MAX_CHARGE_IN_SHELLS = AttachmentRegistry.create(
            wid("max_charge_in_shells"),
            builder -> builder
                    .initializer(() -> ChargeShellItem.DEFAULT_CAPACITY)
                    .syncWith(PacketCodecs.INTEGER, AttachmentSyncPredicate.targetOnly())
                    .persistent(Codecs.POSITIVE_INT));

    public static void init() {
    }
}
