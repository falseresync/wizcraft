package ru.falseresync.wizcraft.common.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;
import ru.falseresync.wizcraft.lib.names.WizBlockNames;
import ru.falseresync.wizcraft.lib.registry.RegistryObject;

import static ru.falseresync.wizcraft.lib.IdUtil.wizId;

public class WizBlockEntities {
    public static final @RegistryObject BlockEntityType<MagicCauldronBlockEntity> MAGIC_CAULDRON = FabricBlockEntityTypeBuilder.create(MagicCauldronBlockEntity::new, WizBlocks.MAGIC_CAULDRON).build();
}
