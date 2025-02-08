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
                entries.add(WizcraftItems.GRIMOIRE);

                entries.add(WizcraftItems.MORTAR_AND_PESTLE);

                entries.add(WizcraftItems.METALLIZED_STICK);
                entries.add(WizcraftItems.WAND_CORE);

                entries.add(WizcraftItems.WAND);

                entries.add(WizcraftItems.STARSHOOTER_FOCUS);
                entries.add(WizcraftItems.CHARGING_FOCUS);
                entries.add(WizcraftItems.LIGHTNING_FOCUS);
                entries.add(WizcraftItems.COMET_WARP_FOCUS);
                entries.add(WizcraftItems.ENERGY_VEIL_FOCUS);

                entries.add(WizcraftItems.TRUESEER_GOGGLES);
                entries.add(WizcraftItems.FOCUSES_BELT);
                entries.add(WizcraftItems.CHARGE_SHELL);

                entries.add(WizcraftItems.CRUCIBLE);
                entries.add(WizcraftItems.LENS);
                entries.add(WizcraftItems.WORKTABLE);
                entries.add(WizcraftItems.LENSING_PEDESTAL);
            })
            .build();
}
