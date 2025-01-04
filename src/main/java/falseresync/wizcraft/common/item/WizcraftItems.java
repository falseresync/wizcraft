package falseresync.wizcraft.common.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import java.util.function.Function;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftItems {
    public static final WandItem WAND = r("wand", WandItem::new, new Item.Settings().maxCount(1));
    public static final Item.Settings FOCUS_SETTINGS = new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON);
    public static final StarshooterFocusItem STARSHOOTER_FOCUS = r("starshooter_focus", StarshooterFocusItem::new, FOCUS_SETTINGS);
    public static final ChargingFocusItem CHARGING_FOCUS = r("charging_focus", ChargingFocusItem::new, FOCUS_SETTINGS);
    public static final LightningFocusItem LIGHTNING_FOCUS = r("lightning_focus", LightningFocusItem::new, FOCUS_SETTINGS);
    public static final CometWarpFocusItem COMET_WARP_FOCUS = r("comet_warp_focus", CometWarpFocusItem::new, FOCUS_SETTINGS);

    private static <T extends Item> T r(String id, Function<Item.Settings, T> item, Item.Settings settings) {
        var fullId = wid(id);
        return Registry.register(Registries.ITEM, fullId, item.apply(settings));
    }

    public static void init() {
    }
}
