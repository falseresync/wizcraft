package falseresync.wizcraft.common.data.attachment;

import com.google.common.base.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.util.dynamic.*;
import org.jetbrains.annotations.*;

public record ChargeShellsAttachment(int currentCharge, IntList shells, int maxCharge) {
    public static final int MAX_SHELLS = 3;
    public static final int MAX_CHARGE = 1000;

    public static final Codec<ChargeShellsAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NONNEGATIVE_INT.fieldOf("maxCharge").forGetter(ChargeShellsAttachment::currentCharge),
            Codecs.NONNEGATIVE_INT.sizeLimitedListOf(MAX_SHELLS)
                    .xmap(it -> (IntList) new IntImmutableList(it), it -> it)
                    .fieldOf("shells").forGetter(ChargeShellsAttachment::shells),
            Codecs.rangedInt(0, MAX_CHARGE).fieldOf("maxCharge").forGetter(ChargeShellsAttachment::maxCharge)
    ).apply(instance, ChargeShellsAttachment::new));

    public static final PacketCodec<RegistryByteBuf, ChargeShellsAttachment> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ChargeShellsAttachment::currentCharge,
            PacketCodecs.collection(IntArrayList::new, PacketCodecs.INTEGER, MAX_SHELLS), ChargeShellsAttachment::shells,
            PacketCodecs.INTEGER, ChargeShellsAttachment::maxCharge,
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
