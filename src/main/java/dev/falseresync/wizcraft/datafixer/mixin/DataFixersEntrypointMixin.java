package dev.falseresync.wizcraft.datafixer.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import dev.falseresync.wizcraft.datafixer.SchemaVersionComponent;
import dev.falseresync.wizcraft.datafixer.WizcraftSchemas;
import dev.falseresync.wizcraft.datafixer.WizcraftServerDataFix;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.datafixer.DataFixTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataFixTypes.class)
public final class DataFixersEntrypointMixin {
    @Final
    @Shadow
    private DSL.TypeReference typeReference;

    @Inject(method = "update(Lcom/mojang/datafixers/DataFixer;Lcom/mojang/serialization/Dynamic;II)Lcom/mojang/serialization/Dynamic;",
            at = @At("RETURN"),
            cancellable = true)
    private <T> void wizcraft$update(DataFixer dataFixer, Dynamic<T> dynamic, int oldVersion, int newVersion, CallbackInfoReturnable<Dynamic<T>> cir) {
        cir.setReturnValue(
                switch (FabricLoader.getInstance().getEnvironmentType()) {
                    case CLIENT -> wizcraft$updateClient(cir.getReturnValue());
                    case SERVER -> wizcraft$updateServer(cir.getReturnValue());
                });
    }

    @Unique
    private <T> Dynamic<T> wizcraft$updateClient(Dynamic<T> dynamic) {
        var integratedServer = MinecraftClient.getInstance().getServer();
        if (integratedServer == null) return dynamic;

        var oldVersion = WizcraftSchemas.SCHEMA_VERSION_COMPONENT
                .maybeGet(integratedServer.getOverworld().getLevelProperties())
                .map(SchemaVersionComponent::get)
                .orElse(0);

        return WizcraftSchemas.FIXER.update(typeReference, dynamic, oldVersion, WizcraftSchemas.CURRENT_SCHEMA_VERSION);
    }

    @Unique
    private <T> Dynamic<T> wizcraft$updateServer(Dynamic<T> dynamic) {
        if (WizcraftServerDataFix.cachedWorld == null) return dynamic;

        var oldVersion = WizcraftSchemas.SCHEMA_VERSION_COMPONENT
                .maybeGet(WizcraftServerDataFix.cachedWorld.getLevelProperties())
                .map(SchemaVersionComponent::get)
                .orElse(0);

        return WizcraftSchemas.FIXER.update(typeReference, dynamic, oldVersion, WizcraftSchemas.CURRENT_SCHEMA_VERSION);
    }
}
