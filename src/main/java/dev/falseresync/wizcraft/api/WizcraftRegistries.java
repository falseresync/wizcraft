package dev.falseresync.wizcraft.api;

import dev.falseresync.wizcraft.api.common.report.Report;
import dev.falseresync.wizcraft.api.common.wand.focus.FocusType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import static dev.falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftRegistries {
    public static final SimpleRegistry<FocusType<?>> FOCUS_TYPE =
            FabricRegistryBuilder
                    .<FocusType<?>>createSimple(RegistryKey.ofRegistry(wid("focus_types")))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();
    public static final SimpleRegistry<Report> REPORTS =
            FabricRegistryBuilder
                    .<Report>createSimple(RegistryKey.ofRegistry(wid("reports")))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static void register() {
    }
}
