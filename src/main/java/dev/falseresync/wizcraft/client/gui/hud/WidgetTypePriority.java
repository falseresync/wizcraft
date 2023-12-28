package dev.falseresync.wizcraft.client.gui.hud;

public enum WidgetTypePriority {
    NORMAL(0),
    HIGH(1);

    private final int value;

    WidgetTypePriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
