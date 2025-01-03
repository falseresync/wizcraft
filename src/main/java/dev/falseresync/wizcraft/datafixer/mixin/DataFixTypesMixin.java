package dev.falseresync.wizcraft.datafixer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import dev.falseresync.wizcraft.datafixer.WizcraftDataFixer;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataFixTypes.class)
public abstract class DataFixTypesMixin {
    @WrapOperation(
            method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/datafixers/DataFixer;update(Lcom/mojang/datafixers/DSL$TypeReference;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;"))
    private <T> Dynamic<T> wizcraft$update(DataFixer instance, DSL.TypeReference typeReference, Dynamic<T> dynamic, int oldVersion, int newVersion, Operation<Dynamic<T>> original) {
        var vanillaUpdated = original.call(instance, typeReference, dynamic, oldVersion, newVersion);
        if (dynamic.getOps() instanceof NbtOps) {
            return WizcraftDataFixer.getFixer().update(
                    typeReference,
                    vanillaUpdated,
                    WizcraftDataFixer.getDataVersion((NbtCompound) dynamic.getValue()),
                    WizcraftDataFixer.DATA_VERSION);
        }

        return vanillaUpdated;
    }
}
