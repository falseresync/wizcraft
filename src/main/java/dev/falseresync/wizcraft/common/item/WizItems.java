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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class WizItems {
    public static final SkyWandItem SKY_WAND;
    public static final SimpleFocusItem<ChargingFocus> CHARGING_FOCUS;
    public static final SimpleFocusItem<StarshooterFocus> STARSHOOTER_FOCUS;
    public static final SimpleFocusItem<LightningFocus> LIGHTNING_FOCUS;
    public static final SimpleFocusItem<CometWarpFocus> COMET_WARP_FOCUS;
    public static final BlockItem ENERGIZED_WORKTABLE;
    public static final BlockItem LENSING_PEDESTAL;
    public static final ItemGroup GROUP_WIZCRAFT;
    private static final FabricItemSettings SIMPLE_FOCUS_SETTINGS;
    private static final FabricItemSettings SIMPLE_BLOCK_ITEM_SETTINGS;
    private static final Map<Identifier, Item> ITEMS_TO_REGISTER = new HashMap<>();
    private static final Map<Identifier, ItemGroup> ITEM_GROUPS_TO_REGISTER = new HashMap<>();

    static {
        SKY_WAND = r(new SkyWandItem(new FabricItemSettings().maxCount(1)));

        SIMPLE_FOCUS_SETTINGS = new FabricItemSettings().maxCount(1);
        CHARGING_FOCUS = rSimpleFocus(WizFocusTypes.CHARGING);
        STARSHOOTER_FOCUS = rSimpleFocus(WizFocusTypes.STARSHOOTER);
        LIGHTNING_FOCUS = rSimpleFocus(WizFocusTypes.LIGHTNING);
        COMET_WARP_FOCUS = rSimpleFocus(WizFocusTypes.COMET_WARP);

        SIMPLE_BLOCK_ITEM_SETTINGS = new FabricItemSettings();
        ENERGIZED_WORKTABLE = rBlockItem(WizBlocks.ENERGIZED_WORKTABLE);
        LENSING_PEDESTAL = rBlockItem(WizBlocks.LENSING_PEDESTAL);

        GROUP_WIZCRAFT = r("wizcraft", FabricItemGroup.builder()
                .icon(SKY_WAND::getDefaultStack)
                .displayName(Text.translatable("itemGroup.wizcraft"))
                .entries((displayContext, entries) -> {
                    entries.add(SKY_WAND);
                    entries.add(STARSHOOTER_FOCUS);
                    entries.add(LIGHTNING_FOCUS);
                    entries.add(COMET_WARP_FOCUS);
                    entries.add(ENERGIZED_WORKTABLE);
                    entries.add(LENSING_PEDESTAL);
                })
                .build());
    }

    private static <T extends Block & HasId> BlockItem rBlockItem(T block) {
        return r(block.getId(), new BlockItem(block, SIMPLE_BLOCK_ITEM_SETTINGS));
    }

    private static <T extends Focus> SimpleFocusItem<T> rSimpleFocus(FocusType<T> type) {
        return r(new SimpleFocusItem<>(SIMPLE_FOCUS_SETTINGS, type));
    }

    private static <T extends Item & HasId> T r(T item) {
        ITEMS_TO_REGISTER.put(item.getId(), item);
        return item;
    }

    private static <T extends Item> T r(String id, T item) {
        return r(new Identifier(Wizcraft.MODID, id), item);
    }

    private static <T extends Item> T r(Identifier id, T item) {
        ITEMS_TO_REGISTER.put(id, item);
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
