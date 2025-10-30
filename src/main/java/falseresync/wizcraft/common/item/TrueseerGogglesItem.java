package falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import falseresync.wizcraft.common.data.WizcraftAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TrueseerGogglesItem extends TrinketItem {
    public TrueseerGogglesItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        if (entity instanceof Player player) {
            player.setAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES, true);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        if (entity instanceof Player player) {
            player.removeAttached(WizcraftAttachments.HAS_TRUESEER_GOGGLES);
        }
    }
}
