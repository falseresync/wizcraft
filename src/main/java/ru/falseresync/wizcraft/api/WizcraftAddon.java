package ru.falseresync.wizcraft.api;

public interface WizcraftAddon {
    void beforeWizcraft();
    void afterWizcraft(WizcraftApi wizcraftApi);
}
