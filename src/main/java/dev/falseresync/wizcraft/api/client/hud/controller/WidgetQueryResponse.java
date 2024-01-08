package dev.falseresync.wizcraft.api.client.hud.controller;

import org.jetbrains.annotations.Nullable;

public record WidgetQueryResponse<T>(
        @Nullable T widget,
        Status status
) {
    public enum Status {
        EXISTS,
        CREATED,
        SLOT_OCCUPIED,
        INSUFFICIENT_PRIORITY
    }
}
