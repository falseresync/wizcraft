package falseresync.wizcraft.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record ChargeComponent(
        int current,
        int max
) {
    public static final Codec<ChargeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NONNEGATIVE_INT.fieldOf("current").forGetter(ChargeComponent::current),
            Codecs.POSITIVE_INT.fieldOf("max").forGetter(ChargeComponent::max)
    ).apply(instance, ChargeComponent::new));
    public static final PacketCodec<RegistryByteBuf, ChargeComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ChargeComponent::current,
            PacketCodecs.INTEGER, ChargeComponent::max,
            ChargeComponent::new);
}
