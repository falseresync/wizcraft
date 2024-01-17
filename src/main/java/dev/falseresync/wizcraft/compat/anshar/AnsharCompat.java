package dev.falseresync.wizcraft.compat.anshar;

import com.lgmrszd.anshar.beacon.IBeaconComponent;
import com.lgmrszd.anshar.transport.PlayerTransportComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public sealed abstract class AnsharCompat {
    private static AnsharCompat INSTANCE = null;

    public static void load() {
        if (FabricLoader.getInstance().isModLoaded("anshar")) {
            INSTANCE = new Present();
        } else {
            INSTANCE = new Absent();
        }
    }

    public static AnsharCompat get() {
        return INSTANCE;
    }

    public abstract boolean tryEnterNetwork(World world, BlockPos pos, PlayerEntity player);

    public static final class Present extends AnsharCompat {
        @Override
        public boolean tryEnterNetwork(World world, BlockPos pos, PlayerEntity player) {
            return world.getBlockEntity(pos, BlockEntityType.BEACON)
                    .flatMap(IBeaconComponent.KEY::maybeGet)
                    .flatMap(IBeaconComponent::getFrequencyNetwork)
                    .flatMap(network -> PlayerTransportComponent.KEY.maybeGet(player)
                            .map(playerTransport -> {
                                playerTransport.enterNetwork(network, pos);
                                return true;
                            }))
                    .isPresent();
        }
    }

    public static final class Absent extends AnsharCompat {
        @Override
        public boolean tryEnterNetwork(World world, BlockPos pos, PlayerEntity player) {
            return false;
        }
    }
}
