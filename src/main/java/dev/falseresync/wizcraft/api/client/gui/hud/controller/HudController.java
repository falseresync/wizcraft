package dev.falseresync.wizcraft.api.client.gui.hud.controller;

import dev.falseresync.wizcraft.api.client.gui.hud.slot.HudSlot;
import dev.falseresync.wizcraft.api.client.gui.hud.slot.WidgetTypePriority;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class HudController<T extends WWidget, WidgetCreationArgument> {
    protected final HudSlot slot;
    protected final WidgetTypePriority typePriority;
    protected final Function<WidgetCreationArgument, T> factory;
    private final float displayTicksMultiplier;
    protected T instance;
    protected WidgetInstancePriority instancePriority = WidgetInstancePriority.NORMAL;
    protected int remainingDisplayTicks = 0;

    public HudController(HudSlot slot, WidgetTypePriority typePriority, Function<WidgetCreationArgument, T> factory) {
        this(slot, typePriority, factory, 1);
    }

    public HudController(HudSlot slot, WidgetTypePriority typePriority, Function<WidgetCreationArgument, T> factory, float displayTicksMultiplier) {
        this.slot = slot;
        this.typePriority = typePriority;
        this.factory = factory;
        this.displayTicksMultiplier = displayTicksMultiplier;
    }

    public void tick() {
        if (remainingDisplayTicks > 0) {
            remainingDisplayTicks -= 1;
        }
    }

    public void clear() {
        CottonHud.remove(instance);
        instancePriority = WidgetInstancePriority.NORMAL;
        instance = null;
    }

    public void resetDisplayTicks() {
        remainingDisplayTicks = calculateDisplayTicks();
    }

    public WidgetQueryResponse<T> getOrCreate(WidgetCreationArgument argument) {
        return getOrCreate(argument, WidgetInstancePriority.NORMAL);
    }

    public WidgetQueryResponse<T> getOrCreate(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        return getOrCreate(argument, priority, false);
    }

    public WidgetQueryResponse<T> override(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        return getOrCreate(argument, priority, true);
    }

    public WidgetQueryResponse<T> getOrCreate(WidgetCreationArgument argument, WidgetInstancePriority priority, boolean shouldOverride) {
        if (instance != null) {
            if (!shouldOverride) {
                return new WidgetQueryResponse<>(instance, WidgetQueryResponse.Status.EXISTS);
            }

            if (instancePriority.getValue() > priority.getValue()) {
                return new WidgetQueryResponse<>(null, WidgetQueryResponse.Status.INSUFFICIENT_PRIORITY);
            }

            return checkSlotAndCreate(argument, priority);
        }

        return checkSlotAndCreate(argument, priority);
    }

    @ApiStatus.Internal
    protected WidgetQueryResponse<T> checkSlotAndCreate(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        var instance = instantiate(argument);
        var size = sizeOf(instance);
        if (!slot.canOccupy(size, typePriority)) {
            return new WidgetQueryResponse<>(null, WidgetQueryResponse.Status.SLOT_OCCUPIED);
        }

        if (slot.isOccupied(size)) {
            slot.clear();
        }

        updateTrackedDataAndNotifyAll(instance, size, priority);
        return new WidgetQueryResponse<>(instance, WidgetQueryResponse.Status.CREATED);
    }

    @ApiStatus.Internal
    protected void updateTrackedDataAndNotifyAll(T instance, Vec2i size, WidgetInstancePriority priority) {
        this.instance = instance;
        instancePriority = priority;
        remainingDisplayTicks = calculateDisplayTicks();
        slot.occupy(this, typePriority, size);
        CottonHud.add(instance, slot.getPositioner());
    }

    protected T instantiate(WidgetCreationArgument argument) {
        return factory.apply(argument);
    }

    public int getRemainingDisplayTicks() {
        return remainingDisplayTicks;
    }

    protected int calculateDisplayTicks() {
        return (int) (displayTicksMultiplier * 40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }

    protected static <W extends WWidget> Vec2i sizeOf(W widget) {
        return new Vec2i(widget.getWidth(), widget.getHeight());
    }

    public static class Aware<ST extends WWidget & ControllerAwareWidget, A> extends HudController<ST, A> {
        public Aware(HudSlot slot, WidgetTypePriority typePriority, Function<A, ST> factory) {
            super(slot, typePriority, factory);
        }

        public Aware(HudSlot slot, WidgetTypePriority typePriority, Function<A, ST> factory, float displayTicksMultiplier) {
            super(slot, typePriority, factory, displayTicksMultiplier);
        }

        @Override
        public void tick() {
            super.tick();
            if (instance != null) {
                instance.controllerTick(remainingDisplayTicks);
            }
        }

        @Override
        protected void updateTrackedDataAndNotifyAll(ST instance, Vec2i size, WidgetInstancePriority priority) {
            super.updateTrackedDataAndNotifyAll(instance, size, priority);
            instance.setController(this);
        }
    }
}
