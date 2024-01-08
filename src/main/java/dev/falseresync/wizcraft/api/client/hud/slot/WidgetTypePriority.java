package dev.falseresync.wizcraft.api.client.hud.slot;

public enum WidgetTypePriority {
    NORMAL(0),
    HIGH(1);

    private final int value;

    WidgetTypePriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
