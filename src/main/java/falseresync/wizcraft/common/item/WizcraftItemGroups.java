package falseresync.wizcraft.common.item;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public class WizcraftItemGroups {
    public static final @RegistryObject ItemGroup GENERAL = FabricItemGroup.builder()
            .icon(WizcraftItems.WAND::getDefaultStack)
            .displayName(Text.translatable("itemGroup.wizcraft"))
            .entries((displayContext, entries) -> {
                entries.add(WizcraftItems.WAND);
                entries.add(WizcraftItems.STARSHOOTER_FOCUS);
            })
            .build();
}
