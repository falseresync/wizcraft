package dev.falseresync.common;

import dev.falseresync.common.Wizcraft;
import dev.falseresync.common.skywand.focus.Focus;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class WizRegistries {
    public static final SimpleRegistry<Focus> FOCUSES;

    static {
        FOCUSES = FabricRegistryBuilder
                .<Focus>createSimple(RegistryKey.ofRegistry(new Identifier(Wizcraft.MODID, "focuses")))
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }

    public static void register() {}
}
