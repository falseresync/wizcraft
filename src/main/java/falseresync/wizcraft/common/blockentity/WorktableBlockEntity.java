package falseresync.wizcraft.common.blockentity;

import falseresync.wizcraft.common.Reports;
import falseresync.wizcraft.common.item.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public abstract class WorktableBlockEntity extends BlockEntity {
    public WorktableBlockEntity(BlockEntityType<? extends WorktableBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected static void onInterrupted(WorktableBlockEntity worktable, World world, BlockPos pos) {
        // TODO: bad effects
        world.playSound(null, pos, SoundEvents.ENTITY_HORSE_BREATHE, SoundCategory.BLOCKS, 1f, 1f);
        Reports.addSmoke(world, pos.toCenterPos().add(0, 0.75, 0));
        var stopSoundPacket = new StopSoundS2CPacket(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP.value().getId(), SoundCategory.BLOCKS);
        for (var player : PlayerLookup.tracking(worktable)) {
            player.networkHandler.sendPacket(stopSoundPacket);
        }
    }

    public abstract SimpleInventory getInventory();

    public abstract InventoryStorage getStorage();

    public abstract void activate(PlayerEntity player);

    public abstract void remove(World world, BlockPos pos);

    public boolean shouldExchangeFor(ItemStack stack) {
        return !stack.isOf(WizcraftItems.WAND);
    }

    public boolean canBeActivatedBy(ItemStack stack) {
        return stack.isIn(WizcraftItemTags.WANDS);
    }
}
