package falseresync.wizcraft.common.data;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

public record ChargeShellsAttachment(int currentCharge, IntList shells, int maxCharge) {
    public static final int MAX_SHELLS = 3;
    public static final int MAX_CHARGE = 1000;

    public static final Codec<ChargeShellsAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("currentCharge").forGetter(ChargeShellsAttachment::currentCharge),
            ExtraCodecs.NON_NEGATIVE_INT.sizeLimitedListOf(MAX_SHELLS)
                    .xmap(it -> (IntList) new IntImmutableList(it), it -> it)
                    .fieldOf("shells").forGetter(ChargeShellsAttachment::shells),
            ExtraCodecs.intRange(0, MAX_CHARGE).fieldOf("maxCharge").forGetter(ChargeShellsAttachment::maxCharge)
    ).apply(instance, ChargeShellsAttachment::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeShellsAttachment> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ChargeShellsAttachment::currentCharge,
            ByteBufCodecs.collection(IntArrayList::new, ByteBufCodecs.INT, MAX_SHELLS), ChargeShellsAttachment::shells,
            ByteBufCodecs.INT, ChargeShellsAttachment::maxCharge,
            ChargeShellsAttachment::new
    );

    public ChargeShellsAttachment {
        Preconditions.checkArgument(currentCharge >= 0 && currentCharge <= maxCharge, "Current charge must be between 0 and max charge");
        Preconditions.checkArgument(shells.size() < MAX_SHELLS, "There can only be %s shells".formatted(MAX_SHELLS));
        Preconditions.checkArgument(maxCharge < MAX_CHARGE, "There can only be %s charge in all shells in total".formatted(MAX_CHARGE));
        Preconditions.checkArgument(shells.intStream().sum() == maxCharge, "The max charge must be the same as the sum of shell charges");
    }

    public static ChargeShellsAttachment createDefault() {
        return new ChargeShellsAttachment(0, IntList.of(), 0);
    }

    @Nullable
    public ChargeShellsAttachment withShell(int shellCharge) {
        if (canAddShell(shellCharge)) {
            var newList = new IntArrayList(shells);
            newList.add(shellCharge);
            return new ChargeShellsAttachment(currentCharge, new IntImmutableList(newList), Math.min(newList.intStream().sum(), MAX_CHARGE));
        } else {
            return null;
        }
    }

    public boolean canAddShell(int shellCharge) {
        return shellCharge > 0 && (maxCharge < MAX_CHARGE || shells.size() < MAX_SHELLS);
    }

    public boolean areShellsFull() {
        return currentCharge == maxCharge;
    }

    @Nullable
    public ChargeShellsAttachment withChargeChange(int chargeChange) {
        if (canChangeCharge(chargeChange)) {
            return new ChargeShellsAttachment(Math.clamp(currentCharge + chargeChange, 0, maxCharge), shells, maxCharge);
        } else {
            return null;
        }
    }

    public boolean canChangeCharge(int chargeChange) {
        return chargeChange < maxCharge && currentCharge + chargeChange >= 0;
    }
}
