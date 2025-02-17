package falseresync.wizcraft.common.data.component;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.util.dynamic.*;

public record ItemBarComponent(int step, int color) {
    public static final ItemBarComponent DEFAULT = new ItemBarComponent(0, 0);
    public static final Codec<ItemBarComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 13).fieldOf("step").forGetter(ItemBarComponent::step),
            Codecs.ARGB.fieldOf("color").forGetter(ItemBarComponent::color)
    ).apply(instance, ItemBarComponent::new));
    public static final PacketCodec<RegistryByteBuf, ItemBarComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ItemBarComponent::step,
            PacketCodecs.INTEGER, ItemBarComponent::color,
            ItemBarComponent::new
    );
}
