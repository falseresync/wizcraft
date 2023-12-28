package dev.falseresync.wizcraft.client.gui.hud;

import org.jetbrains.annotations.Nullable;

public record WidgetQueryResponse<T>(
        @Nullable T widget,
        Status status
) {
    public enum Status {
        EXISTS,
        CREATED,
        SLOT_OCCUPIED
    }
}
