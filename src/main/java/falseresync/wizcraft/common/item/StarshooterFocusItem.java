package falseresync.wizcraft.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            var fireball = new FireballEntity(world, user, Vec3d.ZERO, 1);
            var rotation = user.getRotationVec(1);
            var orthogonalDistance = 1;
            fireball.setPosition(user.getX() + rotation.x * orthogonalDistance, user.getEyeY(), user.getZ() + rotation.z * orthogonalDistance);
            fireball.setVelocity(user, user.getPitch(), user.getYaw(), 0, 1.5F, 1F);
            world.spawnEntity(fireball);
        }
        return super.focusUse(wandStack, focusStack, world, user, hand);
    }
}
