package dev.falseresync.wizcraft.common.mixin;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.tab.TabManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(YACLScreen.class)
public abstract class YACLSaveButtonMixin {
    @Shadow
    private boolean pendingChanges;

    @Shadow
    @Final
    public TabManager tabManager;

    @Inject(method = "finishOrSave", at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER), remap = false)
    public void tempSaveButtonFix(CallbackInfo ci)
    {
        this.pendingChanges = false;

        if (this.tabManager.getCurrentTab() instanceof YACLScreen.CategoryTab categoryTab)
            categoryTab.updateButtons();
    }
}
