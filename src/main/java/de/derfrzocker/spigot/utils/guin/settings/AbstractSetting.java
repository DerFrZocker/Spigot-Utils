package de.derfrzocker.spigot.utils.guin.settings;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractSetting<R extends AbstractSetting<R>> implements Setting<R> {

    protected final Set<Setting<?>> list = new LinkedHashSet<>();
    protected Setting<?> parent;

    protected abstract Object get0(String key);

    protected abstract Set<String> getKeys0(String key);

    protected abstract R createEmptySetting();

    @Override
    public Object get(String key) {
        Object result;

        for (Setting<?> setting : list) {
            result = setting.get(key);
            if (result != null) {
                return result;
            }
        }

        if (parent != null) {
            result = parent.get(key);
        } else {
            result = get0(key);
        }

        return result;
    }

    @Override
    public <V> V get(Object identifier, String key, V defaultValue) {
        Object result = null;

        if (identifier != null) {
            result = get(identifier + "." + key);
        }

        if (result == null) {
            return get(key, defaultValue);
        }

        return (V) result;
    }

    @Override
    public <V> V get(String key, V defaultValue) {
        Object result = get(key);
        return result == null ? defaultValue : (V) result;
    }

    @Override
    public R withSetting(Setting<?> setting) {
        R newSetting = createEmptySetting();
        newSetting.parent = this;

        if (setting != null) {
            newSetting.list.add(setting);
        }

        return newSetting;
    }

    @Override
    public Set<String> getKeys(String key) {
        Set<String> keys = null;
        Set<String> result;
        if (parent != null) {
            result = parent.getKeys(key);
        } else {
            result = getKeys0(key);
        }

        if (result != null) {
            keys = new LinkedHashSet<>(result);
        }

        for (Setting<?> setting : list) {
            result = setting.getKeys(key);
            if (result != null) {
                if (keys == null) {
                    keys = new LinkedHashSet<>();
                }
                keys.addAll(result);
            }
        }

        return keys;
    }

    @Override
    public Set<String> getKeys(Object identifier, String key) {
        Set<String> result = null;

        if (identifier != null) {
            result = getKeys(identifier + "." + key);
        }

        if (result == null) {
            return getKeys(key);
        }

        return result;
    }
}
