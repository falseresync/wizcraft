package falseresync.wizcraft.common.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static falseresync.wizcraft.common.Wizcraft.wid;

public class WizcraftBlockTags {
    public static final TagKey<Block> CRUCIBLE_HEAT_SOURCES = TagKey.create(Registries.BLOCK, wid("crucible_heat_sources"));

    public static void init() {
    }
}
