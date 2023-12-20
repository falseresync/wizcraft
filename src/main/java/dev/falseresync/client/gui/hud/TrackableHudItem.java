package dev.falseresync.client.gui.hud;

import dev.falseresync.client.gui.hud.widget.RemovableHudWidget;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class TrackableHudItem<TrackedWidget extends WWidget & RemovableHudWidget, WidgetCreationArgument> {
    protected final Tracker<TrackedWidget> tracker = new Tracker<>();

    protected abstract boolean compareByArgument(TrackedWidget widget, WidgetCreationArgument argument);

    protected abstract TrackedWidget createWidget(WidgetCreationArgument argument);

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

        var widget = tracker.getWidget().get();
        if (compareByArgument(widget, argument)) {
            widget.resetTicksToRemoval();
            tracker.setReplaceable(replaceable);
            return true;
        }

        if (tracker.isReplaceable()) {
            remove(widget);
            createAndTrack(argument, replaceable);
            return true;
        }

        return false;
    }

    protected void remove(TrackedWidget widget) {
        tracker.clear();
        CottonHud.remove(widget);
    }

    protected void createAndTrack(WidgetCreationArgument argument, boolean replaceable) {
        var widget = createWidget(argument);
        tracker.setWidget(widget).setReplaceable(replaceable);
        CottonHud.add(widget, CottonHud.Positioner.horizontallyCentered(20));
    }

    public void tick() {
        tracker.getWidget().ifPresent(widget -> {
            widget.tick();
            if (widget.shouldBeRemoved()) {
                remove(widget);
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
