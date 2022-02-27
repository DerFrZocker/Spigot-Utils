package de.derfrzocker.spigot.utils.dao.yaml;

import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.dao.BasicDao;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public abstract class BasicYamlDao<K, V> extends YamlDao<V> implements BasicDao<K, V>, ReloadAble {

    public BasicYamlDao(final File file) {
        super(file);
    }

    public Set<V> getAll() {
        final Set<String> keys = getYaml().getKeys(false);

        final Set<V> values = new HashSet<>();

        keys.forEach(value -> getFromStringKey(value).ifPresent(values::add));

        return values;
    }

}
