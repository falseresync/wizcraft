package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.common.skywand.focus.WizFocuses;
import dev.falseresync.wizcraft.lib.HasId;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WizItems {
    public static final SkyWandItem SKY_WAND;
    public static final SimpleFocusItem STARSHOOTER_FOCUS;
    public static final SimpleFocusItem LIGHTNING_FOCUS;
    public static final ItemGroup GROUP_WIZCRAFT;
    private static final FabricItemSettings SIMPLE_FOCUS_SETTINGS;
    private static final Map<Identifier, Item> ITEMS_TO_REGISTER = new HashMap<>();
    private static final Map<Identifier, ItemGroup> ITEM_GROUPS_TO_REGISTER = new HashMap<>();

    static {
        SKY_WAND = r(new SkyWandItem(new FabricItemSettings().maxCount(1)));

        SIMPLE_FOCUS_SETTINGS = new FabricItemSettings().maxCount(1);
        STARSHOOTER_FOCUS = rSimpleFocus(WizFocuses.STARSHOOTER);
        LIGHTNING_FOCUS = rSimpleFocus(WizFocuses.LIGHTNING);

        GROUP_WIZCRAFT = r("wizcraft", FabricItemGroup.builder()
                .icon(SKY_WAND::getDefaultStack)
                .displayName(Text.translatable("itemGroup.wizcraft"))
                .entries((displayContext, entries) -> {
                    entries.add(SKY_WAND);
                    entries.add(STARSHOOTER_FOCUS);
                    entries.add(LIGHTNING_FOCUS);
                })
                .build());
    }

    private static <T extends Focus> SimpleFocusItem rSimpleFocus(T focus) {
        return r(new SimpleFocusItem(SIMPLE_FOCUS_SETTINGS, focus));
    }

    private static <T extends Item & HasId> T r(T item) {
        ITEMS_TO_REGISTER.put(item.getId(), item);
        return item;
    }

    private static <T extends Item> T r(String id, T item) {
        ITEMS_TO_REGISTER.put(new Identifier(Wizcraft.MODID, id), item);
        return item;
    }

    private static <T extends ItemGroup> T r(String id, T group) {
        ITEM_GROUPS_TO_REGISTER.put(new Identifier(Wizcraft.MODID, id), group);
        return group;
    }

    public static void registerItems(BiConsumer<Identifier, Item> registrar) {
        ITEMS_TO_REGISTER.forEach(registrar);
    }

    public static void registerItemGroups(BiConsumer<Identifier, ItemGroup> registrar) {
        ITEM_GROUPS_TO_REGISTER.forEach(registrar);
    }
}
