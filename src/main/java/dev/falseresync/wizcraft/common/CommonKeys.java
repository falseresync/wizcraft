package dev.falseresync.wizcraft.common;

public final class CommonKeys {
    public static final String NAMESPACE = "wizcraft";

    public static String namespaced(String key) {
        return NAMESPACE + ":" + key;
    }

    public static final class Namespaced {
        public static final String WAND = namespaced("wand");
        public static final String FOCUS = namespaced("focus");
    }

    public static final String ID = "id";
    public static final String STACK = "stack";
    public static final String WAND = "wand";
    public static final String MAX_CHARGE = "max_charge";
    public static final String CHARGE = "charge";
    public static final String FOCUS = "focus";
    public static final String FOCUS_TYPE = "focus_type";
    public static final String FOCUS_STACK = "focus_stack";
    public static final String CONTROLS_RENDERING = "controls_rendering";
    public static final String LINKED_TO = "linked_to";
    public static final String NON_EMPTY_PEDESTALS = "non_empty_pedestals";
    public static final String CURRENT_RECIPE = "current_recipe";
    public static final String RESULT = "result";
    public static final String CRAFTING_TIME = "crafting_time";
    public static final String REMAINING_CRAFTING_TIME = "remaining_crafting_time";
    public static final String CHARGING = "charging";
    public static final String WORKTABLE = "worktable";
    public static final String PEDESTALS = "pedestals";
}
