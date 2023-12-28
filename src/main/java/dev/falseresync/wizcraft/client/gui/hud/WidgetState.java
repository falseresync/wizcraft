package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.WStateful;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class WidgetState<T extends WWidget, WidgetCreationArgument> {
    protected final WidgetSlot slot;
    protected final Function<WidgetCreationArgument, T> factory;
    protected T widget;
    protected Priority priority = Priority.NORMAL;
    protected int ticksToRemoval = 0;

    public WidgetState(WidgetSlot slot, Function<WidgetCreationArgument, T> factory) {
        this.slot = slot;
        this.factory = factory;
    }

    public void tick() {
        if (ticksToRemoval == 0) {
            clear();
        } else if (ticksToRemoval > 0) {
            ticksToRemoval -= 1;
        }
    }

    public void clear() {
        CottonHud.remove(widget);
        priority = Priority.NORMAL;
        widget = null;
    }

    public void resetTicksToRemoval() {
        ticksToRemoval = calculateTicksToRemoval();
    }

    public QueryResponse<T> getOrCreate(WidgetCreationArgument argument) {
        return getOrCreate(argument, Priority.NORMAL);
    }

    public QueryResponse<T> getOrCreate(WidgetCreationArgument argument, Priority priority) {
        if (this.priority.getValue() > priority.getValue()) {
            return new QueryResponse<>(null, Status.SLOT_OCCUPIED);
        }

        if (widget != null) {
            return new QueryResponse<>(widget, Status.EXISTS);
        }

        create(argument, priority);
        return new QueryResponse<>(widget, Status.CREATED);
    }

    @ApiStatus.Internal
    protected void create(WidgetCreationArgument argument, Priority priority) {
        widget = factory.apply(argument);
        this.priority = priority;
        ticksToRemoval = calculateTicksToRemoval();
        CottonHud.add(widget, slot.positioner());
    }

    protected int calculateTicksToRemoval() {
        return (int) (40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }

    public static class ForStateful<ST extends WWidget & WStateful, A> extends WidgetState<ST, A> {
        public ForStateful(WidgetSlot slot, Function<A, ST> factory) {
            super(slot, factory);
        }

        @Override
        public void tick() {
            super.tick();
            if (widget != null) {
                widget.statefulTick(ticksToRemoval);
            }
        }
    }

    public enum Priority {
        NORMAL(0),
        HIGH(1);

        private final int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Status {
        EXISTS,
        CREATED,
        SLOT_OCCUPIED
    }

    public record QueryResponse<T>(
            @Nullable T widget,
            Status status
    ) {
    }
}
