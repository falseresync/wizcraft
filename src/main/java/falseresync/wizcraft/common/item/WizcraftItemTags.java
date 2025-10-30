package falseresync.wizcraft.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftItemTags {
    public static final TagKey<Item> FOCUSES = TagKey.create(Registries.ITEM, wid("focuses"));
    public static final TagKey<Item> WANDS = TagKey.create(Registries.ITEM, wid("wands"));

    public static void init() {
    }
}
