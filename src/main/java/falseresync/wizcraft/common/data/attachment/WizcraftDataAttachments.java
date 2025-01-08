package falseresync.wizcraft.common.data.attachment;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.PacketCodecs;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftDataAttachments {
    public static final AttachmentType<Boolean> HAS_TRUESEER_GOGGLES = AttachmentRegistry.create(
            wid("has_trueseer_goggles"),
            builder -> builder
                    .syncWith(PacketCodecs.BOOL, AttachmentSyncPredicate.targetOnly())
                    .persistent(Codec.BOOL));

    public static void init() {
    }
}
