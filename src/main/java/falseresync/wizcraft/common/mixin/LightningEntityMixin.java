package falseresync.wizcraft.common.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import falseresync.wizcraft.common.item.focus.LightningFocusItem;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin implements LightningFocusItem.WizcraftLightning {
    @Unique
    private static final TrackedData<Boolean> THUNDERLESS = DataTracker.registerData(LightningEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
                    ordinal = 0))
    private boolean wizcraft$tick$removeThunder(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        return !((LightningEntity) (Object) this).getDataTracker().get(THUNDERLESS);
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
                    ordinal = 1))
    private void wizcraft$tick$changeSoundCategory(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        if (((LightningEntity) (Object) this).getDataTracker().get(THUNDERLESS)) {
            original.call(instance, x, y, z, sound, SoundCategory.PLAYERS, 1.0f, pitch, true);
        } else {
            original.call(instance, x, y, z, sound, category, volume, pitch, useDistance);
        }
    }


    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void wizcraft$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(THUNDERLESS, false);
    }

    @Override
    public void wizcraft$setThunderless() {
        ((LightningEntity) (Object) this).getDataTracker().set(THUNDERLESS, true, true);
    }
}
