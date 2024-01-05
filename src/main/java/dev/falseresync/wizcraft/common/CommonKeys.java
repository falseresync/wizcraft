package dev.falseresync.wizcraft.common;

public final class CommonKeys {
    public static final String NAMESPACE = "wizcraft";

    public static String namespaced(String key) {
        return NAMESPACE + ":" + key;
    }

    public static final class Namespaced {
        public static final String SKY_WAND = namespaced("sky_wand");
        public static final String FOCUS = namespaced("focus");
    }

    public static final String STACK = "stack";
    public static final String SKY_WAND = "sky_wand";
    public static final String MAX_CHARGE = "max_charge";
    public static final String CHARGE = "charge";
    public static final String FOCUS = "focus";
    public static final String FOCUS_TYPE = "focus_type";
    public static final String FOCUS_STACK = "focus_stack";
}
