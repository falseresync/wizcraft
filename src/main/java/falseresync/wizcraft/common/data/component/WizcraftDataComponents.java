package falseresync.wizcraft.common.data.component;

import com.mojang.serialization.DataResult;
import falseresync.lib.registry.RegistryObject;
import falseresync.wizcraft.common.item.FocusItem;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.GlobalPos;

public class WizcraftDataComponents {
    public static final @RegistryObject ComponentType<ItemStack> EQUIPPED_FOCUS_ITEM =
            ComponentType.<ItemStack>builder()
                    .codec(ItemStack.CODEC.validate(stack -> stack.getItem() instanceof FocusItem
                            ? DataResult.success(stack)
                            : DataResult.error(() -> "Only FocusItems are allowed")))
                    .packetCodec(ItemStack.PACKET_CODEC)
                    .build();
    public static final @RegistryObject ComponentType<Integer> WAND_CHARGE =
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> WAND_MAX_CHARGE =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> FOCUS_USE_COST =
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> CHARGING_FOCUS_PROGRESS =
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<GlobalPos> WARP_FOCUS_ANCHOR =
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build();
}
