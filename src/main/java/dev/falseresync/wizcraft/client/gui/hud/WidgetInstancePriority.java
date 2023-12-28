package dev.falseresync.wizcraft.client.gui.hud;

public enum WidgetInstancePriority {
    NORMAL(0),
    HIGH(1);

    private final int value;

    WidgetInstancePriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
