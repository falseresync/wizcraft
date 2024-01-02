package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.api.client.gui.hud.controller.HudController;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetInstancePriority;
import dev.falseresync.wizcraft.api.client.gui.hud.controller.WidgetQueryResponse;
import dev.falseresync.wizcraft.api.client.gui.hud.slot.HudSlot;
import dev.falseresync.wizcraft.client.gui.hud.slot.TopLeftHudSlot;
import dev.falseresync.wizcraft.client.gui.hud.slot.UnderBossBarHudSlot;
import dev.falseresync.wizcraft.api.client.gui.hud.slot.WidgetTypePriority;
import dev.falseresync.wizcraft.client.gui.hud.widget.WFocusPicker;
import dev.falseresync.wizcraft.client.gui.hud.widget.WWandChargeBar;
import dev.falseresync.wizcraft.client.gui.hud.widget.WLabelWithSFX;
import dev.falseresync.wizcraft.common.Wizcraft;
import dev.falseresync.wizcraft.common.item.WizItems;
import dev.falseresync.wizcraft.common.skywand.SkyWand;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Deque;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class WizHud {
    public static final class Slots {
        public static final HudSlot UNDER_BOSS_BAR = new UnderBossBarHudSlot();
        public static final HudSlot TOP_LEFT = new TopLeftHudSlot();
    }

    public static final HudController<WFocusPicker, Deque<ItemStack>> FOCUS_PICKER;
    public static final HudController<WLabelWithSFX, Text> STATUS_MESSAGE;
    public static final HudController<WWandChargeBar, SkyWand> WAND_CHARGE_BAR;

    static {
        FOCUS_PICKER = new HudController.Aware<>(Slots.UNDER_BOSS_BAR, WidgetTypePriority.HIGH, WFocusPicker::new);
        STATUS_MESSAGE = new HudController.Aware<>(Slots.UNDER_BOSS_BAR, WidgetTypePriority.NORMAL, text -> {
            var widget = new WLabelWithSFX(text);
            widget.enableFade();
            widget.enableShadow();
            widget.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return widget;
        });
        WAND_CHARGE_BAR = new HudController.Aware<>(Slots.TOP_LEFT, WidgetTypePriority.NORMAL, WWandChargeBar::new);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            Slots.UNDER_BOSS_BAR.tick();
            Slots.TOP_LEFT.tick();

            updateWandChargeBar(client);
        });
    }

    public static void init() {}

    protected static void updateWandChargeBar(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        var mainHandStack = client.player.getMainHandStack();
        if (!mainHandStack.isOf(WizItems.SKY_WAND)) {
            return;
        }

        var wand = SkyWand.fromStack(mainHandStack);
        var chargeBar = WAND_CHARGE_BAR.getOrCreate(wand, WidgetInstancePriority.HIGH);
        if (chargeBar.status() == WidgetQueryResponse.Status.EXISTS && chargeBar.widget() != null) {
            WAND_CHARGE_BAR.resetDisplayTicks();
            chargeBar.widget().updateValue(wand.getCharge());
        }
    }
}
