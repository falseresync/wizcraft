package falseresync.wizcraft.common.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftItems {
    public static final WandItem WAND = r("wand", WandItem::new, new Item.Settings());
    public static final StarshooterFocusItem STARSHOOTER_FOCUS = r("starshooter_focus", StarshooterFocusItem::new, new Item.Settings());
    public static final ChargingFocusItem CHARGING_FOCUS = r("charging_focus", ChargingFocusItem::new, new Item.Settings());

    private static <T extends Item> T r(String id, Function<Item.Settings, T> item, Item.Settings settings) {
        var fullId = wid(id);
        return Registry.register(Registries.ITEM, fullId, item.apply(settings));
    }

    public static void init() {
    }
}
