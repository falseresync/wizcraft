package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WizcraftItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public WizcraftItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(WizcraftItemTags.FOCUSES).add(FOCUSES);
        getOrCreateTagBuilder(WizcraftItemTags.WANDS)
                .add(WizcraftItems.WAND);
    }

    public static final Item[] FOCUSES = new Item[] {
            WizcraftItems.STARSHOOTER_FOCUS,
            WizcraftItems.CHARGING_FOCUS,
            WizcraftItems.LIGHTNING_FOCUS,
            WizcraftItems.COMET_WARP_FOCUS,
            WizcraftItems.ENERGY_VEIL_FOCUS
    };
}
