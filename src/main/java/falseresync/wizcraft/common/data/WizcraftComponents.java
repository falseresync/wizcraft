package falseresync.wizcraft.common.data;

import com.mojang.serialization.*;
import falseresync.lib.registry.*;
import falseresync.wizcraft.common.item.focus.*;
import net.minecraft.component.*;
import net.minecraft.item.*;
import net.minecraft.network.codec.*;
import net.minecraft.util.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;

import java.util.*;

public class WizcraftComponents {
    // Generic
    public static final @RegistryObject ComponentType<ItemBarComponent> ITEM_BAR =
            ComponentType.<ItemBarComponent>builder().codec(ItemBarComponent.CODEC).packetCodec(ItemBarComponent.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<Boolean> IN_USE =
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final @RegistryObject ComponentType<Boolean> TOOLTIP_OVERRIDDEN =
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final @RegistryObject ComponentType<InventoryComponent> INVENTORY =
            ComponentType.<InventoryComponent>builder().codec(InventoryComponent.CODEC).packetCodec(InventoryComponent.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<Integer> INVENTORY_SIZE =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<UUID> UUID =
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build();

    // Wand
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
    public static final @RegistryObject ComponentType<Integer> SHELL_CAPACITY =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> CHARGE_DEFICIT =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();

    // Focuses
    public static final @RegistryObject ComponentType<Integer> FOCUS_PLATING =
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, FocusPlating.values().length - 1)).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> CHARGING_FOCUS_PROGRESS =
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<GlobalPos> WARP_FOCUS_ANCHOR =
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<GlobalPos> WARP_FOCUS_PERSISTENT_ANCHOR =
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<Boolean> WARP_FOCUS_BLOCK_ONLY_MODE =
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final @RegistryObject ComponentType<UUID> ENERGY_VEIL_UUID =
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build();
}
