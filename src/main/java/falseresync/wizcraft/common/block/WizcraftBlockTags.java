package falseresync.wizcraft.common.block;

import net.minecraft.block.*;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.*;

import static falseresync.wizcraft.common.Wizcraft.*;

public class WizcraftBlockTags {
    public static final TagKey<Block> CRUCIBLE_HEAT_SOURCES = TagKey.of(RegistryKeys.BLOCK, wid("crucible_heat_sources"));

    public static void init() {
    }
}
