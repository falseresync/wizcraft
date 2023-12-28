package dev.falseresync.wizcraft.client.gui.oldhud;

import dev.falseresync.wizcraft.client.gui.oldhud.widget.WRemovable;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class TrackedHudWidget<TrackedWidget extends WWidget & WRemovable, WidgetCreationArgument> {
    protected final Tracker<TrackedWidget> tracker = new Tracker<>();

    protected abstract boolean compare(TrackedWidget widget, WidgetCreationArgument argument);

    protected abstract TrackedWidget create(WidgetCreationArgument argument);

    protected abstract CottonHud.Positioner getPositionerFor(TrackedWidget widget);

    public Optional<TrackedWidget> get() {
        return tracker.getWidget();
    }

    public Optional<TrackedWidget> setOrReplace(WidgetCreationArgument argument) {
        return setOrReplace(argument, true);
    }

    public Optional<TrackedWidget> setOrReplace(WidgetCreationArgument argument, boolean replaceable) {
        if (tracker.getWidget().isEmpty()) {
            createAndTrack(argument, replaceable);
            return tracker.getWidget();
        }

        var currentWidget = tracker.getWidget().get();
        if (compare(currentWidget, argument)) {
            currentWidget.resetTicksToRemoval();
            tracker.setReplaceable(replaceable);
            return tracker.getWidget();
        }

        if (tracker.isReplaceable()) {
            removeAndClear(currentWidget);
            createAndTrack(argument, replaceable);
            return tracker.getWidget();
        }

        return Optional.empty();
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
