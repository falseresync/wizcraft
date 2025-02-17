package falseresync.wizcraft.common.item;

import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftItemTags {
    public static final TagKey<Item> FOCUSES = TagKey.of(RegistryKeys.ITEM, wid("focuses"));
    public static final TagKey<Item> WANDS = TagKey.of(RegistryKeys.ITEM, wid("wands"));

    public static void init() {
    }
}
