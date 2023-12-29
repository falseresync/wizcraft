package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.WControllerAware;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class WidgetController<T extends WWidget, WidgetCreationArgument> {
    protected final WidgetSlot slot;
    protected final WidgetTypePriority typePriority;
    protected final Function<WidgetCreationArgument, T> factory;
    private final float displayTicksMultiplier;
    protected T instance;
    protected WidgetInstancePriority instancePriority = WidgetInstancePriority.NORMAL;
    protected int remainingDisplayTicks = 0;

    public WidgetController(WidgetSlot slot, WidgetTypePriority typePriority, Function<WidgetCreationArgument, T> factory) {
        this(slot, typePriority, factory, 1);
    }

    public WidgetController(WidgetSlot slot, WidgetTypePriority typePriority, Function<WidgetCreationArgument, T> factory, float displayTicksMultiplier) {
        this.slot = slot;
        this.typePriority = typePriority;
        this.factory = factory;
        this.displayTicksMultiplier = displayTicksMultiplier;
    }

    public void tick() {
        if (this.remainingDisplayTicks > 0) {
            this.remainingDisplayTicks -= 1;
        }
    }


    public void clear() {
        CottonHud.remove(this.instance);
        this.instancePriority = WidgetInstancePriority.NORMAL;
        this.instance = null;
    }

    public void resetDisplayTicks() {
        this.remainingDisplayTicks = calculateDisplayTicks();
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
        if (this.instance != null) {
            if (!shouldOverride) {
                return new WidgetQueryResponse<>(this.instance, WidgetQueryResponse.Status.EXISTS);
            }

            if (this.instancePriority.getValue() > priority.getValue()) {
                return new WidgetQueryResponse<>(null, WidgetQueryResponse.Status.INSUFFICIENT_PRIORITY);
            }

            return checkSlotAndCreate(argument, priority);
        }

        return checkSlotAndCreate(argument, priority);
    }

    @ApiStatus.Internal
    protected WidgetQueryResponse<T> checkSlotAndCreate(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        if (!this.slot.canOccupy(this.typePriority)) {
            return new WidgetQueryResponse<>(null, WidgetQueryResponse.Status.SLOT_OCCUPIED);
        }

        if (this.slot.isOccupied()) {
            this.slot.clear();
        }

        create(argument, priority);
        return new WidgetQueryResponse<>(this.instance, WidgetQueryResponse.Status.CREATED);
    }

    @ApiStatus.Internal
    protected void create(WidgetCreationArgument argument, WidgetInstancePriority priority) {
        this.instance = this.factory.apply(argument);
        this.instancePriority = priority;
        this.remainingDisplayTicks = calculateDisplayTicks();
        this.slot.occupy(this, this.typePriority);
        CottonHud.add(this.instance, this.slot.getPositioner());
    }

    public int getRemainingDisplayTicks() {
        return this.remainingDisplayTicks;
    }

    protected int calculateDisplayTicks() {
        return (int) (this.displayTicksMultiplier * 40 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
    }

    public static class Aware<ST extends WWidget & WControllerAware, A> extends WidgetController<ST, A> {
        public Aware(WidgetSlot slot, WidgetTypePriority typePriority, Function<A, ST> factory) {
            super(slot, typePriority, factory);
        }

        public Aware(WidgetSlot slot, WidgetTypePriority typePriority, Function<A, ST> factory, float displayTicksMultiplier) {
            super(slot, typePriority, factory, displayTicksMultiplier);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.instance != null) {
                this.instance.controllerTick(this.remainingDisplayTicks);
            }
        }

        @Override
        protected void create(A a, WidgetInstancePriority priority) {
            super.create(a, priority);
            this.instance.setController(this);
        }
    }
}
