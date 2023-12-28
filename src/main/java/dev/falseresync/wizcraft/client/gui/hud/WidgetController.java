package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.WControllerAware;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

public class WidgetController<T extends WWidget, WidgetCreationArgument> {
    protected final WidgetSlot slot;
    protected final Function<WidgetCreationArgument, T> factory;
    private final float displayTicksMultiplier;
    protected T instance;
    protected WidgetInstancePriority priority = WidgetInstancePriority.NORMAL;
    protected int remainingDisplayTicks = 0;

    public WidgetController(WidgetSlot slot, Function<WidgetCreationArgument, T> factory) {
        this(slot, factory, 1);
    }

    public WidgetController(WidgetSlot slot, Function<WidgetCreationArgument, T> factory, float displayTicksMultiplier) {
        this.slot = slot;
        this.factory = factory;
        this.displayTicksMultiplier = displayTicksMultiplier;
    }

    public void tick() {
        if (remainingDisplayTicks == 0) {
            clear();
        } else if (remainingDisplayTicks > 0) {
            remainingDisplayTicks -= 1;
        }
    }

    public void clear() {
        CottonHud.remove(instance);
        priority = WidgetInstancePriority.NORMAL;
        instance = null;
    }

    public void resetDisplayTicks() {
        remainingDisplayTicks = calculateDisplayTicks();
    }

    public WidgetQueryResponse<T> getOrCreate(WidgetCreationArgument argument) {
        return getOrCreate(argument, WidgetInstancePriority.NORMAL);
    }

    public WidgetQueryResponse<T> getOrCreate(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        if (this.priority.getValue() > priority.getValue()) {
            return new WidgetQueryResponse<>(null, WidgetQueryResponse.Status.SLOT_OCCUPIED);
        }

        if (instance != null) {
            return new WidgetQueryResponse<>(instance, WidgetQueryResponse.Status.EXISTS);
        }

        create(argument, priority);
        return new WidgetQueryResponse<>(instance, WidgetQueryResponse.Status.CREATED);
    }

    @ApiStatus.Internal
    protected void create(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        instance = factory.apply(argument);
        this.priority = priority;
        remainingDisplayTicks = calculateDisplayTicks();
        CottonHud.add(instance, slot.positioner());
    }

    protected int calculateDisplayTicks() {
        return (int) (displayTicksMultiplier * 40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }

    public static class Aware<ST extends WWidget & WControllerAware, A> extends WidgetController<ST, A> {
        public Aware(WidgetSlot slot, Function<A, ST> factory) {
            super(slot, factory);
        }

        public Aware(WidgetSlot slot, Function<A, ST> factory, float displayTicksMultiplier) {
            super(slot, factory, displayTicksMultiplier);
        }

        @Override
        public void tick() {
            super.tick();
            if (instance != null) {
                instance.controllerTick(remainingDisplayTicks);
            }
        }
    }
}
