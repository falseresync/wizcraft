package dev.falseresync.wizcraft.datafixer.mixin;

import dev.falseresync.wizcraft.datafixer.WizcraftDataFixer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtHelper.class)
public abstract class NbtHelperMixin {
    @Inject(
            method = "putDataVersion(Lnet/minecraft/nbt/NbtCompound;I)Lnet/minecraft/nbt/NbtCompound;",
            at = @At("HEAD"))
    private static void wizcraft$putCurrentDataVersion(NbtCompound nbt, int dataVersion, CallbackInfoReturnable<NbtCompound> cir) {
        WizcraftDataFixer.putDataVersion(nbt);
    }
}
