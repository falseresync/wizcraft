package falseresync.wizcraft.common.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import falseresync.wizcraft.common.data.attachment.WizcraftDataAttachments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TrueseerGogglesItem extends TrinketItem {
    public TrueseerGogglesItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            player.setAttached(WizcraftDataAttachments.HAS_TRUESEER_GOGGLES, true);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            player.removeAttached(WizcraftDataAttachments.HAS_TRUESEER_GOGGLES);
        }
    }
}
