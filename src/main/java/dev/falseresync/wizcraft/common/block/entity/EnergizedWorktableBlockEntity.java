package dev.falseresync.wizcraft.common.block.entity;

import dev.falseresync.wizcraft.client.gui.hud.WizHud;
import dev.falseresync.wizcraft.common.recipe.WizRecipes;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnergizedWorktableBlockEntity extends BlockEntity {
    public static final int PEDESTALS_SEARCH_COOLDOWN = 5;
    protected final List<LensingPedestalBlockEntity> pedestals = new ArrayList<>();
    protected final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    protected final InventoryStorage storage = InventoryStorage.of(inventory, null);
    protected int remainingPedestalsSearchCooldown = 0;

    public EnergizedWorktableBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.ENERGIZED_WORKTABLE, pos, state);
        inventory.addListener(sender -> markDirty());
    }

    public static void tick(World world, BlockPos pos, BlockState state, EnergizedWorktableBlockEntity worktable) {
        if (world.isClient()) {
            return;
        }

        if (worktable.remainingPedestalsSearchCooldown > 0) {
            worktable.remainingPedestalsSearchCooldown -= 1;
        } else {
            searchPedestals(world, pos, worktable);
        }
    }

    protected static void searchPedestals(World world, BlockPos pos, EnergizedWorktableBlockEntity worktable) {
        worktable.remainingPedestalsSearchCooldown = PEDESTALS_SEARCH_COOLDOWN;
        worktable.pedestals.clear();

        for (var pedestalPos : List.of(pos.north(2), pos.west(2), pos.south(2), pos.east(2))) {
            if (world.getBlockEntity(pedestalPos) instanceof LensingPedestalBlockEntity pedestal) {
                worktable.pedestals.add(pedestal);
            }
        }
    }

    protected static void reportNotEnoughPedestals(World world, PlayerEntity user) {
        if (world.isClient()) {
            user.playSoundIfNotSilent(SoundEvents.BLOCK_LEVER_CLICK);
            WizHud.STATUS_MESSAGE.getOrCreate(Text.translatable("hud.wizcraft.sky_wand.not_enough_pedestals"));
        }
    }

    public void craft(@Nullable PlayerEntity player) {
        if (world == null) {
            return;
        }

        searchPedestals(world, pos, this);
        if (pedestals.size() < 4) {
            if (player != null) {
                reportNotEnoughPedestals(world, player);
            }
            return;
        }

        var combinedInventory = new SimpleInventory(pedestals.size() + 1);
        combinedInventory.setStack(0, inventory.getStack(0));
        for (int i = 0; i < pedestals.size(); i++) {
            combinedInventory.setStack(i + 1, pedestals.get(i).getStorage().getSlot(0).getResource().toStack());
        }

        var result = world.getRecipeManager()
                .getFirstMatch(WizRecipes.LENSED_WORKTABLE, combinedInventory, world)
                .map(RecipeEntry::value)
                .map(recipe -> recipe.craft(combinedInventory, world.getRegistryManager()))
                .orElse(ItemStack.EMPTY);

        if (result.isEmpty()) {
            return;
        }

        pedestals.forEach(LensingPedestalBlockEntity::onCrafted);
        inventory.setStack(0, result);
        if (player != null) {
            reportSuccess();
        }
    }

    protected void reportSuccess() {
        if (world != null && !world.isClient()) {
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        System.out.printf("W %s %s%n", pos, inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory.getHeldStacks());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory.getHeldStacks());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public InventoryStorage getStorage() {
        return storage;
    }
}
