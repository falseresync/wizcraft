package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.WFocusPicker;
import dev.falseresync.wizcraft.common.Wizcraft;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Deque;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class WizHud {
    protected static WFocusPicker focusPicker = null;
    protected static int focusPickerTicksToRemoval = 0;

    static {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (focusPickerTicksToRemoval > 0) {
                focusPickerTicksToRemoval -= 1;

                if (focusPickerTicksToRemoval == 0) {
                    CottonHud.remove(focusPicker);
                    focusPicker = null;
                }
            }
        });
    }

    public static WidgetQuery<WFocusPicker> getOrCreateFocusPicker(Deque<ItemStack> focuses) {
        if (focusPicker != null) {
            focusPickerTicksToRemoval = calculateDefaultTicksToRemoval();
            return new WidgetQuery<>(focusPicker, WidgetStatus.EXISTS);
        }

        focusPicker = new WFocusPicker(focuses);
        CottonHud.add(focusPicker, CottonHud.Positioner.horizontallyCentered(20));
        return new WidgetQuery<>(focusPicker, WidgetStatus.CREATED);
    }

    protected static int calculateDefaultTicksToRemoval() {
        return (int) (40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }

    public static class WidgetState<T extends WWidget, WidgetCreationArgument> {
        protected T widget;
        protected int ticksToRemoval = 0;
        protected final Function<WidgetCreationArgument, T> factory;

        public WidgetState(Function<WidgetCreationArgument, T> factory) {
            this.factory = factory;
        }

        public void tick() {
            if (ticksToRemoval == 0) {
                CottonHud.remove(widget);
                widget = null;
            } else if (ticksToRemoval > 0) {
                ticksToRemoval -= 1;
            }
        }

        public void resetTicksToRemoval() {
            ticksToRemoval = calculateDefaultTicksToRemoval();
        }

        public WidgetQuery<T> getOrCreate(WidgetCreationArgument argument) {
            if (widget != null) {
                ticksToRemoval = calculateDefaultTicksToRemoval();
                return new WidgetQuery<>(widget, WidgetStatus.EXISTS);
            }

            widget = factory.apply(argument);
            CottonHud.add(widget, CottonHud.Positioner.horizontallyCentered(20));
            return new WidgetQuery<>(widget, WidgetStatus.CREATED);
        }
    }

    public record WidgetQuery<T>(
            T widget,
            WidgetStatus status
    ) {
    }

    public enum WidgetStatus {
        EXISTS,
        CREATED
    }
}
