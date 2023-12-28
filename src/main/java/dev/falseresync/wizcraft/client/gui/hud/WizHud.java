package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.WFocusPicker;
import dev.falseresync.wizcraft.client.gui.hud.widget.WWandChargeBar;
import dev.falseresync.wizcraft.client.gui.hud.widget.WLabelWithSFX;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Deque;

@Environment(EnvType.CLIENT)
public class WizHud {
    protected static final WidgetSlot UNDER_BOSS_BAR = new WidgetSlot(CottonHud.Positioner.horizontallyCentered(20));
    protected static final WidgetSlot TOP_LEFT = new WidgetSlot(CottonHud.Positioner.of(5, 5));
    public static final WidgetState<WFocusPicker, Deque<ItemStack>> FOCUS_PICKER;
    public static final WidgetState<WLabelWithSFX, Text> STATUS_MESSAGE;
    public static final WidgetState<WWandChargeBar, SkyWand> WAND_CHARGE_BAR;

    static {
        FOCUS_PICKER = new WidgetState.ForStateful<>(UNDER_BOSS_BAR, WFocusPicker::new);
        STATUS_MESSAGE = new WidgetState.ForStateful<>(UNDER_BOSS_BAR, text -> {
            var widget = new WLabelWithSFX(text);
            widget.enableFade();
            widget.enableShadow();
            widget.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return widget;
        });
        WAND_CHARGE_BAR = new WidgetState.ForStateful<>(TOP_LEFT, WWandChargeBar::new, 0.5f);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            FOCUS_PICKER.tick();
            STATUS_MESSAGE.tick();
            WAND_CHARGE_BAR.tick();

            handleWandChargeBar(client);
        });
    }

    public static void init() {}

    protected static void handleWandChargeBar(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        var mainHandStack = client.player.getMainHandStack();
        if (!mainHandStack.isOf(WizItems.SKY_WAND)) {
            return;
        }

        var wand = SkyWand.fromStack(mainHandStack);
        var chargeBar = WAND_CHARGE_BAR.getOrCreate(wand, WidgetState.Priority.HIGH);
        if (chargeBar.status() == WidgetState.Status.EXISTS && chargeBar.widget() != null) {
            WAND_CHARGE_BAR.resetTicksToRemoval();
            chargeBar.widget().updateValue(wand.getCharge());
        }
    }
}
