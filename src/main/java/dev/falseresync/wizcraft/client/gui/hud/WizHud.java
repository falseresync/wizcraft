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
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class WizHud {
    protected static final WidgetSlot UNDER_BOSS_BAR = new WidgetSlot() {
        private static final CottonHud.Positioner DEFAULT_POSITIONER = CottonHud.Positioner.horizontallyCentered(8);
        private static final Function<Integer, CottonHud.Positioner> SHIFTED_POSITIONER =
                (numberOfBossBars) -> CottonHud.Positioner.horizontallyCentered(4 + 20 * numberOfBossBars);

        @Override
        public CottonHud.Positioner getPositioner() {
            var bossBars = MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars;
            return bossBars.isEmpty()
                    ? DEFAULT_POSITIONER
                    : SHIFTED_POSITIONER.apply(bossBars.size());
        }
    };

    protected static final WidgetSlot TOP_LEFT = new WidgetSlot() {
        private static final CottonHud.Positioner DEFAULT_POSITIONER = CottonHud.Positioner.of(4, 4);

        @Override
        public CottonHud.Positioner getPositioner() {
            return DEFAULT_POSITIONER;
        }
    };

    public static final WidgetController<WFocusPicker, Deque<ItemStack>> FOCUS_PICKER;
    public static final WidgetController<WLabelWithSFX, Text> STATUS_MESSAGE;
    public static final WidgetController<WWandChargeBar, SkyWand> WAND_CHARGE_BAR;

    static {
        FOCUS_PICKER = new WidgetController.Aware<>(UNDER_BOSS_BAR, WidgetTypePriority.HIGH, WFocusPicker::new);
        STATUS_MESSAGE = new WidgetController.Aware<>(UNDER_BOSS_BAR, WidgetTypePriority.NORMAL, text -> {
            var widget = new WLabelWithSFX(text);
            widget.enableFade();
            widget.enableShadow();
            widget.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return widget;
        });
        WAND_CHARGE_BAR = new WidgetController.Aware<>(TOP_LEFT, WidgetTypePriority.NORMAL, WWandChargeBar::new);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            UNDER_BOSS_BAR.tick();
            TOP_LEFT.tick();

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
