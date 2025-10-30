package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WorktableBlockEntity extends BlockEntity {
    public WorktableBlockEntity(BlockEntityType<? extends WorktableBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected static void onInterrupted(WorktableBlockEntity worktable, Level world, BlockPos pos) {
        // TODO: bad effects
        world.playSound(null, pos, SoundEvents.HORSE_BREATHE, SoundSource.BLOCKS, 1f, 1f);
        Reports.addSmoke(world, pos.getCenter().add(0, 0.75, 0));
        var stopSoundPacket = new ClientboundStopSoundPacket(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value().getLocation(), SoundSource.BLOCKS);
        for (var player : PlayerLookup.tracking(worktable)) {
            player.connection.send(stopSoundPacket);
        }
    }

    public abstract SimpleContainer getInventory();

    public abstract InventoryStorage getStorage();

    public abstract void activate(Player player);

    public abstract void remove(Level world, BlockPos pos);

    public boolean shouldExchangeFor(ItemStack stack) {
        return !stack.is(WizcraftItems.WAND);
    }

    public boolean canBeActivatedBy(ItemStack stack) {
        return stack.is(WizcraftItemTags.WANDS);
    }
}
