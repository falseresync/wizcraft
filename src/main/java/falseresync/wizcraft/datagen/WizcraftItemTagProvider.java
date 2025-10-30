package falseresync.wizcraft.datagen;

import falseresync.wizcraft.common.item.WizcraftItemTags;
import falseresync.wizcraft.common.item.WizcraftItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class WizcraftItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public static final Item[] FOCUSES = new Item[]{
            WizcraftItems.STARSHOOTER_FOCUS,
            WizcraftItems.CHARGING_FOCUS,
            WizcraftItems.LIGHTNING_FOCUS,
            WizcraftItems.COMET_WARP_FOCUS,
            WizcraftItems.ENERGY_VEIL_FOCUS
    };

    public WizcraftItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(WizcraftItemTags.FOCUSES).add(FOCUSES);
        getOrCreateTagBuilder(WizcraftItemTags.WANDS)
                .add(WizcraftItems.WAND);
    }
}
