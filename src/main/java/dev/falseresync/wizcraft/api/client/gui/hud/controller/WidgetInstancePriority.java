package dev.falseresync.wizcraft.api.client.gui.hud.controller;

public enum WidgetInstancePriority {
    NORMAL(0),
    HIGH(1);

    private final int value;

    WidgetInstancePriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
