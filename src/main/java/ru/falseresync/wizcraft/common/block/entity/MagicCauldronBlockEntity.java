package ru.falseresync.wizcraft.common.block.entity;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.item.impl.DirectFixedItemInv;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.falseresync.wizcraft.api.WizcraftApi;
import ru.falseresync.wizcraft.api.element.ElementAmount;
import ru.falseresync.wizcraft.common.Wizcraft;
import ru.falseresync.wizcraft.common.init.WizBlockEntities;
import ru.falseresync.wizcraft.lib.names.WizNbtNames;

public class MagicCauldronBlockEntity extends BlockEntity {
    public final DirectFixedItemInv itemInv;
    public final SimpleFixedFluidInv fluidInv;

    public MagicCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(WizBlockEntities.MAGIC_CAULDRON, pos, state);

        itemInv = new DirectFixedItemInv(1);
        itemInv.addListener(inv -> markDirty(), () -> {});

        fluidInv = new SimpleFixedFluidInv(1, FluidAmount.BUCKET);
        fluidInv.addListener((inv, tank, previous, current) -> markDirty(), () -> {});
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicCauldronBlockEntity entity) {
        if (world.isClient) {
            return;
        }

        var volume = entity.fluidInv.getInvFluid(0);
        if (volume.isEmpty()) {
            return;
        }

        var stack = entity.itemInv.getInvStack(0);
        if (stack.isEmpty()) {
            return;
        }

        var composition = WizcraftApi.getInstance().getCompositionsManager().forItem(stack.getItem());
        if (composition.isPresent()) {
            var count = stack.getCount();
            var elementAmounts = composition.get().elements().stream().map(elementAmount -> new ElementAmount(elementAmount.element(), elementAmount.amount() * count)).toList();
            Wizcraft.LOGGER.info(elementAmounts.toString());
            entity.itemInv.forceSetInvStack(0, ItemStack.EMPTY);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        var customNbt = new NbtCompound();
        fluidInv.toTag(customNbt);
        itemInv.toTag(customNbt);
        nbt.put(WizNbtNames.CUSTOM_NBT, customNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        var customNbt = nbt.getCompound(WizNbtNames.CUSTOM_NBT);
        fluidInv.fromTag(customNbt);
        itemInv.fromTag(customNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}