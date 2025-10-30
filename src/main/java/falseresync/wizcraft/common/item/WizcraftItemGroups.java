package falseresync.wizcraft.common.item;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class WizcraftItemGroups {
    public static final @RegistryObject CreativeModeTab GENERAL = FabricItemGroup.builder()
            .icon(WizcraftItems.WAND::getDefaultInstance)
            .title(Component.translatable("itemGroup.wizcraft"))
            .displayItems((displayContext, entries) -> {
                entries.accept(WizcraftItems.GRIMOIRE);

                entries.accept(WizcraftItems.MORTAR_AND_PESTLE);

                entries.accept(WizcraftItems.METALLIZED_STICK);
                entries.accept(WizcraftItems.WAND_CORE);

                entries.accept(WizcraftItems.WAND);

                entries.accept(WizcraftItems.STARSHOOTER_FOCUS);
                entries.accept(WizcraftItems.CHARGING_FOCUS);
                entries.accept(WizcraftItems.LIGHTNING_FOCUS);
                entries.accept(WizcraftItems.COMET_WARP_FOCUS);
                entries.accept(WizcraftItems.ENERGY_VEIL_FOCUS);

                entries.accept(WizcraftItems.TRUESEER_GOGGLES);
                entries.accept(WizcraftItems.FOCUSES_BELT);
                entries.accept(WizcraftItems.CHARGE_SHELL);

                entries.accept(WizcraftItems.CRUCIBLE);
                entries.accept(WizcraftItems.LENS);
                entries.accept(WizcraftItems.WORKTABLE);
                entries.accept(WizcraftItems.LENSING_PEDESTAL);
            })
            .build();
}
