package falseresync.wizcraft.common.block;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftBlockTags {
    public static final TagKey<Block> CRUCIBLE_HEAT_SOURCES = TagKey.of(RegistryKeys.BLOCK, wid("crucible_heat_sources"));

    public static void init() {
    }
}
