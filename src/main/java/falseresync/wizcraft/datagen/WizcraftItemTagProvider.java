package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class WizcraftItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public WizcraftItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(WizcraftItemTags.FOCUSES)
                .add(WizcraftItems.STARSHOOTER_FOCUS)
                .add(WizcraftItems.CHARGING_FOCUS)
                .add(WizcraftItems.LIGHTNING_FOCUS)
                .add(WizcraftItems.COMET_WARP_FOCUS);
    }
}
