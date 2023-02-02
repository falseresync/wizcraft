package ru.falseresync.wizcraft.common.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import ru.falseresync.wizcraft.common.init.WizBlocks;

public class WandItem extends Item {
    public WandItem() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        if (world.getBlockState(pos).isOf(Blocks.CAULDRON)) {
            world.setBlockState(pos, WizBlocks.MAGIC_CAULDRON.getDefaultState());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
