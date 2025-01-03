package falseresync.wizcraft.common.focus;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import static falseresync.wizcraft.common.Wizcraft.wid;

public abstract class Focus {
    public static final Registry<Focus> REGISTRY = FabricRegistryBuilder
            .<Focus>createSimple(RegistryKey.ofRegistry(wid("focuses")))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public abstract void initComponents(ItemStack stack);

    public abstract void removeComponents(ItemStack stack);
}
