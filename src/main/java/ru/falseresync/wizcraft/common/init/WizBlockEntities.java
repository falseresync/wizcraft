package ru.falseresync.wizcraft.common.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;
import ru.falseresync.wizcraft.lib.autoregistry.RegistryObject;

public class WizBlockEntities {
    public static final @RegistryObject BlockEntityType<MagicCauldronBlockEntity> MAGIC_CAULDRON = FabricBlockEntityTypeBuilder.create(MagicCauldronBlockEntity::new, WizBlocks.MAGIC_CAULDRON).build();
}
