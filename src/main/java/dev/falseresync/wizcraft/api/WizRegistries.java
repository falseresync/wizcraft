package dev.falseresync.wizcraft.api;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import dev.falseresync.wizcraft.api.common.report.Report;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class WizRegistries {
    public static final SimpleRegistry<FocusType<?>> FOCUS_TYPE =
            FabricRegistryBuilder
                    .<FocusType<?>>createSimple(RegistryKey.ofRegistry(new Identifier(Wizcraft.MODID, "focus_types")))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();
    public static final SimpleRegistry<Report> REPORTS =
            FabricRegistryBuilder
                    .<Report>createSimple(RegistryKey.ofRegistry(new Identifier(Wizcraft.MODID, "reports")))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static void register() {
    }
}
