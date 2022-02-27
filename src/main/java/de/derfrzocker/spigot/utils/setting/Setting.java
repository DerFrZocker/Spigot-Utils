package de.derfrzocker.spigot.utils.setting;

import java.util.Set;

public interface Setting {

    Object get(String key);

    <V> V get(Object identifier, String key, V defaultValue);

    <V> V get(String key, V defaultValue);

    Setting withSetting(Setting setting);

    Set<String> getKeys(String key);

    Set<String> getKeys(Object identifier, String key);
}
