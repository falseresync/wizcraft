package falseresync.wizcraft.common.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftItemTags {
    public static final TagKey<Item> FOCUSES = TagKey.of(RegistryKeys.ITEM, wid("focuses"));
    public static final TagKey<Item> WANDS = TagKey.of(RegistryKeys.ITEM, wid("wands"));
    public static final TagKey<Item> ACTIVATORS = TagKey.of(RegistryKeys.ITEM, wid("activators"));

    public static void init() {
    }
}
