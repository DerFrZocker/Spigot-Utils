package de.derfrzocker.spigot.utils.guin.settings;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface Setting<R extends Setting<R>> {

    static <S extends Setting<S>> BiFunction<Setting<?>, S, S> createFunction(Supplier<S> emptySetting) {
        return (newSetting, oldSetting) -> {
            S setting;
            if (oldSetting == null) {
                setting = emptySetting.get();
            } else {
                setting = oldSetting;
            }

            return setting.withSetting(newSetting);
        };
    }

    Object get(String key);

    <V> V get(Object identifier, String key, V defaultValue);

    <V> V get(String key, V defaultValue);

    R withSetting(Setting<?> setting);

    Set<String> getKeys(String key);

    Set<String> getKeys(Object identifier, String key);

}
