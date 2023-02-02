package ru.falseresync.wizcraft.common.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.falseresync.wizcraft.common.block.entity.MagicCauldronBlockEntity;
import ru.falseresync.wizcraft.common.init.WizBlocks;

import static ru.falseresync.wizcraft.lib.IdUtil.id;

public class WizBlockEntities {
    public static final BlockEntityType<MagicCauldronBlockEntity> MAGIC_CAULDRON;

    static {
        MAGIC_CAULDRON = FabricBlockEntityTypeBuilder.create(MagicCauldronBlockEntity::new, WizBlocks.MAGIC_CAULDRON).build();
    }

    public static void register() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id("magic_cauldron"), MAGIC_CAULDRON);
    }
}
