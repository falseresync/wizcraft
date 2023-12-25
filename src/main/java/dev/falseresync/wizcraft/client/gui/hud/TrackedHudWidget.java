package dev.falseresync.wizcraft.client.gui.hud;

import dev.falseresync.wizcraft.client.gui.hud.widget.RemovableHudWidget;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class TrackedHudWidget<TrackedWidget extends WWidget & RemovableHudWidget, WidgetCreationArgument> {
    protected final Tracker<TrackedWidget> tracker = new Tracker<>();

    protected abstract boolean compare(TrackedWidget widget, WidgetCreationArgument argument);

    protected abstract TrackedWidget create(WidgetCreationArgument argument);

    protected abstract CottonHud.Positioner getPositionerFor(TrackedWidget widget);

    public boolean setOrReplace(WidgetCreationArgument argument) {
        return setOrReplace(argument, true);
    }

    /**
     *
     * @param argument
     * @param replaceable
     * @return whether the status label has been set or replaced successfully
     */
    public boolean setOrReplace(WidgetCreationArgument argument, boolean replaceable) {
        if (tracker.getWidget().isEmpty()) {
            createAndTrack(argument, replaceable);
            return true;
        }

        var currentWidget = tracker.getWidget().get();
        if (compare(currentWidget, argument)) {
            currentWidget.resetTicksToRemoval();
            tracker.setReplaceable(replaceable);
            return true;
        }

        if (tracker.isReplaceable()) {
            removeAndClear(currentWidget);
            createAndTrack(argument, replaceable);
            return true;
        }

        return false;
    }

    public void clear() {
        tracker.getWidget().ifPresent(this::removeAndClear);
    }

    protected void removeAndClear(TrackedWidget widget) {
        tracker.clear();
        CottonHud.remove(widget);
    }

    protected void createAndTrack(WidgetCreationArgument argument, boolean replaceable) {
        var widget = create(argument);
        tracker.setWidget(widget).setReplaceable(replaceable);
        CottonHud.add(widget, getPositionerFor(widget));
    }

    public void tick() {
        tracker.getWidget().ifPresent(widget -> {
            widget.tick();
            if (widget.shouldBeRemoved()) {
                removeAndClear(widget);
            }
        });
    }

    protected static class Tracker<W extends WWidget> {
        private W widget;
        private boolean replaceable;

        public Tracker() {
            this(null, true);
        }

        public Tracker(W widget, boolean replaceable) {
            this.widget = widget;
            this.replaceable = replaceable;
        }

        public void clear() {
            widget = null;
            replaceable = true;
        }

        public boolean isReplaceable() {
            return replaceable;
        }

        public Tracker<W> setReplaceable(boolean replaceable) {
            this.replaceable = replaceable;
            return this;
        }

        public Optional<W> getWidget() {
            return Optional.ofNullable(widget);
        }

        public Tracker<W> setWidget(@Nullable W widget) {
            this.widget = widget;
            return this;
        }
    }
}
