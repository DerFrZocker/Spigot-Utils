package de.derfrzocker.spigot.utils.setting;

import java.util.Set;

public class DummySetting extends AbstractSetting<DummySetting> {
    @Override
    protected Object get0(String key) {
        return null;
    }

    @Override
    protected Set<String> getKeys0(String key) {
        return null;
    }

    @Override
    protected DummySetting createEmptySetting() {
        return new DummySetting();
    }
}
