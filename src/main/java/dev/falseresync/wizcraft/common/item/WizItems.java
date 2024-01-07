package dev.falseresync.wizcraft.common.item;

import dev.falseresync.wizcraft.api.HasId;
import dev.falseresync.wizcraft.api.common.skywand.focus.Focus;
import dev.falseresync.wizcraft.api.common.skywand.focus.FocusType;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.block.WizBlocks;
import dev.falseresync.wizcraft.common.skywand.focus.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WizItems {
    public static final SkyWandItem SKY_WAND;
    public static final SimpleFocusItem<ChargingFocus> CHARGING_FOCUS;
    public static final SimpleFocusItem<StarshooterFocus> STARSHOOTER_FOCUS;
    public static final SimpleFocusItem<LightningFocus> LIGHTNING_FOCUS;
    public static final SimpleFocusItem<CometWarpFocus> COMET_WARP_FOCUS;
    public static final BlockItem LENS;
    public static final BlockItem PLATED_WORKTABLE;
    public static final BlockItem LENSING_PEDESTAL;
    public static final ItemGroup GROUP_GENERAL;
    private static final FabricItemSettings SIMPLE_FOCUS_SETTINGS;
    private static final FabricItemSettings SIMPLE_BLOCK_ITEM_SETTINGS;
    private static final Map<Group, List<Item>> GROUP_ENTRIES = new HashMap<>();
    private static final Map<Identifier, Item> ITEMS_TO_REGISTER = new HashMap<>();
    private static final Map<Identifier, ItemGroup> ITEM_GROUPS_TO_REGISTER = new HashMap<>();

    static {
        SKY_WAND = r(new SkyWandItem(new FabricItemSettings().maxCount(1)));

        SIMPLE_FOCUS_SETTINGS = new FabricItemSettings().maxCount(1);
        var chargingFocusItem = new SimpleFocusItem<>(SIMPLE_FOCUS_SETTINGS, WizFocuses.CHARGING);
        CHARGING_FOCUS = r(chargingFocusItem.getId(), chargingFocusItem, null); // charging focus item is only attainable by command
        STARSHOOTER_FOCUS = rSimpleFocus(WizFocuses.STARSHOOTER);
        LIGHTNING_FOCUS = rSimpleFocus(WizFocuses.LIGHTNING);
        COMET_WARP_FOCUS = rSimpleFocus(WizFocuses.COMET_WARP);

        SIMPLE_BLOCK_ITEM_SETTINGS = new FabricItemSettings();
        LENS = rBlockItem(WizBlocks.LENS);
        PLATED_WORKTABLE = rBlockItem(WizBlocks.PLATED_WORKTABLE);
        LENSING_PEDESTAL = rBlockItem(WizBlocks.LENSING_PEDESTAL);

        GROUP_GENERAL = r(new Identifier(Wizcraft.MODID, "general"), FabricItemGroup.builder()
                .icon(SKY_WAND::getDefaultStack)
                .displayName(Text.translatable("itemGroup.wizcraft"))
                .entries((displayContext, entries) -> GROUP_ENTRIES.get(Group.GENERAL).forEach(entries::add))
                .build());
    }

    private static <T extends Block & HasId> BlockItem rBlockItem(T block) {
        return r(block.getId(), new BlockItem(block, SIMPLE_BLOCK_ITEM_SETTINGS));
    }

    private static <T extends Focus> SimpleFocusItem<T> rSimpleFocus(FocusType<T> type) {
        return r(new SimpleFocusItem<>(SIMPLE_FOCUS_SETTINGS, type));
    }

    private static <T extends Item & HasId> T r(T item) {
        return r(item.getId(), item);
    }

    private static <T extends Item> T r(Identifier id, T item) {
        return r(id, item, Group.GENERAL);
    }

    /**
     * @param group passing a null doesn't put the item into any group
     */
    private static <T extends Item> T r(Identifier id, T item, @Nullable Group group) {
        if (group != null) {
            GROUP_ENTRIES.computeIfAbsent(group, key -> new ArrayList<>()).add(item);
        }
        ITEMS_TO_REGISTER.put(id, item);
        return item;
    }

    private static <T extends ItemGroup> T r(Identifier id, T group) {
        ITEM_GROUPS_TO_REGISTER.put(id, group);
        return group;
    }

    private enum Group {
        GENERAL;
    }

    public static void registerItems(BiConsumer<Identifier, Item> registrar) {
        ITEMS_TO_REGISTER.forEach(registrar);
    }

    public static void registerItemGroups(BiConsumer<Identifier, ItemGroup> registrar) {
        ITEM_GROUPS_TO_REGISTER.forEach(registrar);
    }
}
