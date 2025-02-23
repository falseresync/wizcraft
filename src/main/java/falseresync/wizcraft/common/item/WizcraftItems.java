package falseresync.wizcraft.common.item;

import falseresync.wizcraft.common.block.*;
import falseresync.wizcraft.common.item.focus.*;
import io.wispforest.lavender.book.*;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;

import java.util.function.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftItems {
    public static final LavenderBookItem GRIMOIRE = LavenderBookItem.registerForBook(r("grimoire", GrimoireItem::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    public static final Item MORTAR_AND_PESTLE = r("mortar_and_pestle", MortarAndPestleItem::new, new Item.Settings().maxCount(1).maxDamage(16));

    public static final Item METALLIZED_STICK = r("metallized_stick", Item::new, new Item.Settings());
    public static final Item WAND_CORE = r("wand_core", Item::new, new Item.Settings().maxCount(1));

    public static final WandItem WAND = r("wand", WandItem::new, new Item.Settings().maxCount(1));

    public static final Item.Settings FOCUS_SETTINGS = new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON);
    public static final StarshooterFocusItem STARSHOOTER_FOCUS = r("starshooter_focus", StarshooterFocusItem::new, FOCUS_SETTINGS);
    public static final ChargingFocusItem CHARGING_FOCUS = r("charging_focus", ChargingFocusItem::new, FOCUS_SETTINGS);
    public static final LightningFocusItem LIGHTNING_FOCUS = r("lightning_focus", LightningFocusItem::new, FOCUS_SETTINGS);
    public static final CometWarpFocusItem COMET_WARP_FOCUS = r("comet_warp_focus", CometWarpFocusItem::new, FOCUS_SETTINGS);
    public static final EnergyVeilFocusItem ENERGY_VEIL_FOCUS = r("energy_veil_focus", EnergyVeilFocusItem::new, FOCUS_SETTINGS);

    public static final TrueseerGogglesItem TRUESEER_GOGGLES = r("trueseer_goggles", TrueseerGogglesItem::new, new Item.Settings().maxCount(1));
    public static final FocusesBeltItem FOCUSES_BELT = r("focuses_belt", FocusesBeltItem::new, new Item.Settings().maxCount(1));
    public static final ChargeShellItem CHARGE_SHELL = r("charge_shell", ChargeShellItem::new, new Item.Settings().maxCount(1));

    public static final Item.Settings DEFAULT_BLOCK_ITEM_SETTINGS = new Item.Settings();
    public static final BlockItem CRUCIBLE = rBlockItem("crucible", WizcraftBlocks.CRUCIBLE, DEFAULT_BLOCK_ITEM_SETTINGS);
    public static final BlockItem LENS = rBlockItem("lens", WizcraftBlocks.LENS, DEFAULT_BLOCK_ITEM_SETTINGS);
    public static final BlockItem WORKTABLE = rBlockItem("worktable", WizcraftBlocks.DUMMY_WORKTABLE, DEFAULT_BLOCK_ITEM_SETTINGS);
    public static final BlockItem LENSING_PEDESTAL = rBlockItem("lensing_pedestal", WizcraftBlocks.LENSING_PEDESTAL, DEFAULT_BLOCK_ITEM_SETTINGS);


    private static <T extends Item> T r(String id, Function<Item.Settings, T> item, Item.Settings settings) {
        var fullId = wid(id);
        return Registry.register(Registries.ITEM, fullId, item.apply(settings));
    }

    private static <T extends Block> BlockItem rBlockItem(String id, T block, Item.Settings settings) {
        var fullId = wid(id);
        return Registry.register(Registries.ITEM, fullId, new BlockItem(block, settings));
    }

    public static void init() {
    }
}
