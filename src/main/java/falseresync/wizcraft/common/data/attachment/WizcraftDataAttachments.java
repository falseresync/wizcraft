package falseresync.wizcraft.common.data.attachment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static falseresync.wizcraft.common.Wizcraft.wid;

@SuppressWarnings("UnstableApiUsage")
public class WizcraftDataAttachments {
    public static final AttachmentType<BlockPos> LENSING_PEDESTAL_LINKED_TO =
            AttachmentRegistry.create(
                    wid("lensing_pedestal/linked_to"),
                    builder -> builder
//                            .syncWith(BlockPos.PACKET_CODEC, AttachmentSyncPredicate.all())
                            .persistent(BlockPos.CODEC));
    public static final AttachmentType<List<ItemStack>> LENSING_PEDESTAL_INVENTORY =
            AttachmentRegistry.create(
                    wid("lensing_pedestal/inventory"),
                    builder -> builder
//                            .syncWith(ItemStack.OPTIONAL_LIST_PACKET_CODEC, AttachmentSyncPredicate.all())
                            .persistent(ItemStack.OPTIONAL_CODEC.listOf()));

    public static void init() {}
}
